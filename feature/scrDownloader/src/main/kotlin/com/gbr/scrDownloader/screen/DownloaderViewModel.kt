package com.gbr.scrDownloader.screen

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.gbr.common.strings.StringProvider
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.data.repository.GitabasesDescRepository
import com.gbr.data.usecase.CreateDownloadWorkRequestUseCase
import com.gbr.model.download.DownloadStage
import com.gbr.model.gitabase.Gitabase
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.work.DownloadWorkConstants
import com.gbr.scrDownloader.R
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
    private val gitabasesDescRepository: GitabasesDescRepository,
    private val savedStateHandle: SavedStateHandle,
    private val stringProvider: StringProvider,
    private val workManager: WorkManager,
    private val createDownloadWorkRequestUseCase: CreateDownloadWorkRequestUseCase,
    private val gitabaseManager: GitabaseDatabaseManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(DownloaderUiState())
    val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()

    private val _stage = MutableStateFlow<DownloadStage>(DownloadStage.STARTING)
    val stage: StateFlow<DownloadStage> = _stage.asStateFlow()

    private var currentWorkId: UUID? = null

    init {
        restoreState()
        loadLanguages()
    }

    private fun restoreState() {
        val restoredState = savedStateHandle.get<DownloaderUiState>("ui_state")
        if (restoredState != null) {
            _uiState.value = restoredState
        }
    }

    private fun saveState() {
        savedStateHandle["ui_state"] = _uiState.value
    }

    private fun loadLanguages() {
        viewModelScope.launch {
            try {
                val gitabases = gitabasesDescRepository.getDownloadableGitabases()
                val languages = gitabases.map { it.id.lang }.distinct().sortedWith(compareBy<GitabaseLang> { it.value == "eng" || it.value=="rus"}.thenBy { it.value }).reversed()


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

    fun selectLanguage(language: GitabaseLang) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        saveState()
    }

    fun downloadGitabase(gitabase: Gitabase) {
        viewModelScope.launch {
            try {

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
                    destinationPath = gitabaseManager.gitabasesPath
                )

                currentWorkId = workRequest.id
                workManager.enqueue(workRequest)

                workManager.getWorkInfoByIdFlow(workRequest.id)
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
                                _uiState.value = _uiState.value.copy(
                                    downloadingGitabase = null,
                                    downloadedGitabases = _uiState.value.downloadedGitabases + gitabase
                                )
                                saveState()
                                currentWorkId = null
                            }
                            androidx.work.WorkInfo.State.FAILED -> {
                                _uiState.value = _uiState.value.copy(
                                    downloadingGitabase = null,
                                    error = stringProvider.getString(R.string.error_failed_to_download, gitabase.title, "")
                                )
                                saveState()
                                currentWorkId = null
                            }
                            else -> { /* In progress */ }
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    downloadingGitabase = null,
                    error = stringProvider.getString(R.string.error_failed_to_download, gitabase.title, e.message ?: "")
                )
                saveState()
                currentWorkId = null
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
