package com.gbr.scrDownloader.screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import androidx.work.await
import com.gbr.common.strings.StringProvider
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.repository.GitabasesRepository
import com.gbr.datastore.datasource.GbrPreferencesDataSource
import com.gbr.model.download.DownloadStage
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.parseGitabaseID
import com.gbr.model.work.DownloadWorkConstants
import com.gbr.scrDownloader.R
import com.gbr.ui.SnackbarHelper
import com.gbr.work.CreateDownloadWorkRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DownloaderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gitabasesRepository: GitabasesRepository,
    private val gitabasesDescRepository: GitabasesDescRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringProvider: StringProvider,
    private val workManager: WorkManager,
    private val createDownloadWorkRequestUseCase: CreateDownloadWorkRequestUseCase,
    private val gitabaseManager: GitabaseDatabaseManager,
    private val gbrPreferencesDataSource: GbrPreferencesDataSource,
    private val snackbarHelper: SnackbarHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _stage = MutableStateFlow(DownloadStage.STARTING)
    val stage: StateFlow<DownloadStage> = _stage.asStateFlow()

    private var currentWorkId: UUID? = null

    init {
        restoreState()
        loadLanguages()
        restoreWorkId()
    }

    private fun restoreState() {
        var restoredState = savedStateHandle.get<DownloaderUiState>("ui_state")
        restoredState = restoredState?.copy(downloadedGitabases = gitabasesRepository.getAllGitabases())
        if (restoredState != null) {
            _uiState.value = restoredState
        }else{
            _uiState.value = DownloaderUiState(downloadedGitabases = gitabasesRepository.getAllGitabases())
        }
    }

    private fun saveState() {
        savedStateHandle["ui_state"] = _uiState.value
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            try {
                val gitabases = gitabasesDescRepository.getDownloadableGitabases()
                val languages = gitabases.map { it.id.lang }.distinct()
                    .sortedWith(compareBy<GitabaseLang> { it.value == "eng" || it.value == "rus" }.thenBy { it.value })
                    .reversed()


                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    languages = languages,
                    gitabases = gitabases,
                    selectedLanguage = languages.firstOrNull()
                )
                saveState()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: stringProvider.getString(R.string.error_failed_to_load_languages)
                )
                saveState()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun restoreWorkId() {
        viewModelScope.launch {
            val savedWorkId = gbrPreferencesDataSource.getCurrentDownloadWorkId()
            if (savedWorkId != null) {
                // Check the current state of the work first
                val workInfo = workManager.getWorkInfoById(savedWorkId).await()

                when (workInfo?.state) {
                    androidx.work.WorkInfo.State.SUCCEEDED,
                    androidx.work.WorkInfo.State.FAILED,
                    androidx.work.WorkInfo.State.CANCELLED -> {
                        // Work is already finished, clear it from DataStore
                        currentWorkId = null
                        gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                    }
                androidx.work.WorkInfo.State.RUNNING,
                androidx.work.WorkInfo.State.ENQUEUED -> {
                    // Work is still active, restore subscription
                    currentWorkId = savedWorkId

                    // Get Gitabase ID from work progress
                    val gitabaseIdKey = workInfo.progress.getString(DownloadWorkConstants.KEY_GITABASE_ID)
                    if (gitabaseIdKey != null) {
                        val gitabaseId = gitabaseIdKey.parseGitabaseID()
                        if (gitabaseId != null) {
                            // Find the matching Gitabase from the list
                            val matchingGitabase = _uiState.value.gitabases.find { it.id == gitabaseId }
                            if (matchingGitabase != null) {
                                _uiState.value = _uiState.value.copy(
                                    downloadingGitabase = matchingGitabase
                                )
                                saveState()
                            }
                        }
                    }

                    // Immediately update progress and stage from current work info
                    val progressValue = workInfo.progress.getInt(DownloadWorkConstants.KEY_PROGRESS, 0)
                    val stageName = workInfo.progress.getString(DownloadWorkConstants.KEY_STAGE)

                    _progress.value = progressValue
                    stageName?.let {
                        _stage.value = runCatching { DownloadStage.valueOf(it) }
                            .getOrElse { DownloadStage.STARTING }
                    }

                    // Then subscribe to future updates
                    subscribeToWorkStatus(savedWorkId)
                }
                    else -> {
                        // Unknown state or null, clear it to be safe
                        currentWorkId = null
                        gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                    }
                }
            }
        }
    }

    private fun subscribeToWorkStatus(workId: UUID) {
        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(workId)
                .collect { info ->
                    val progressValue = info.progress.getInt(DownloadWorkConstants.KEY_PROGRESS, 0)
                    val stageName = info.progress.getString(DownloadWorkConstants.KEY_STAGE)

                    _progress.value = progressValue
                    stageName?.let {
                        _stage.value = runCatching { DownloadStage.valueOf(it) }
                            .getOrElse { DownloadStage.STARTING }
                    }

                    when (info.state) {
                        androidx.work.WorkInfo.State.SUCCEEDED -> {
                            val downloadingGitabase = _uiState.value.downloadingGitabase
                            if (downloadingGitabase != null) {
                                _uiState.value = _uiState.value.copy(
                                    downloadingGitabase = null,
                                    downloadedGitabases = _uiState.value.downloadedGitabases + downloadingGitabase
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    downloadingGitabase = null
                                )
                            }
                            saveState()
                            currentWorkId = null
                            gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                        }

                        androidx.work.WorkInfo.State.FAILED -> {
                            val downloadingGitabase = _uiState.value.downloadingGitabase
                            _uiState.value = _uiState.value.copy(
                                downloadingGitabase = null,
                                error = if (downloadingGitabase != null) {
                                    stringProvider.getString(
                                        R.string.error_failed_to_download,
                                        downloadingGitabase.title,
                                        ""
                                    )
                                } else {
                                    stringProvider.getString(R.string.error_failed_to_download, "", "")
                                }
                            )
                            saveState()
                            currentWorkId = null
                            gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                        }

                        androidx.work.WorkInfo.State.CANCELLED -> {
                            _uiState.value = _uiState.value.copy(
                                downloadingGitabase = null
                            )
                            saveState()
                            currentWorkId = null
                            gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                        }

                        else -> { /* In progress */
                        }
                    }
                }
        }
    }

    fun selectLanguage(language: GitabaseLang) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        saveState()
    }

    fun downloadGitabase(gitabase: Gitabase) {

            viewModelScope.launch {
                try {
                    if(_uiState.value.downloadedGitabases.contains(gitabase)){
                        snackbarHelper.showMessage(stringProvider.getString(R.string.message_this_gb_already_downloaded))
                        return@launch
                    }

                    if (currentWorkId != null)
                    {
                        snackbarHelper.showMessage(stringProvider.getString(R.string.message_only_one_download_at_once))
                        return@launch
                    }
                    _uiState.value = _uiState.value.copy(
                        downloadingGitabase = gitabase
                    )
                    saveState()

                    val destinationDir = File(context.filesDir, "")
                    if (!destinationDir.exists()) {
                        destinationDir.mkdirs()
                    }

                    val workRequest = createDownloadWorkRequestUseCase(
                        downloadUrl = gitabase.downloadURL.orEmpty(),
                        destinationPath = gitabaseManager.gitabasesPath,
                        gitabaseId = gitabase.id.key
                    )

                    currentWorkId = workRequest.id
                    gbrPreferencesDataSource.setCurrentDownloadWorkId(workRequest.id)
                    workManager.enqueue(workRequest)

                    subscribeToWorkStatus(workRequest.id)
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        downloadingGitabase = null,
                        error = stringProvider.getString(
                            R.string.error_failed_to_download,
                            gitabase.title,
                            e.message ?: ""
                        )
                    )
                    saveState()
                    currentWorkId = null
                    gbrPreferencesDataSource.setCurrentDownloadWorkId(null)
                }
            }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
        saveState()
    }
}

@Parcelize
data class DownloaderUiState(
    val isLoading: Boolean = true,
    val languages: List<GitabaseLang> = emptyList(),
    val gitabases: List<Gitabase> = emptyList(),
    val selectedLanguage: GitabaseLang? = null,
    val downloadingGitabase: Gitabase? = null,
    val downloadedGitabases: Set<Gitabase> = emptySet(),
    val error: String? = null
) : Parcelable
