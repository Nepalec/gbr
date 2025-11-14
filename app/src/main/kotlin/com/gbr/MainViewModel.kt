package com.gbr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gbr.common.strings.StringProvider
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.data.usecase.InitializeGitabasesUseCase
import com.gbr.model.theme.DarkThemeConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val initializeGitabasesUseCase: InitializeGitabasesUseCase,
    userPreferencesRepository: UserPreferencesRepository,
    private val stringProvider: StringProvider
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message =
        MutableStateFlow<String?>(stringProvider.getString(R.string.message_scanning_for_gitabase_files))
    val message: StateFlow<String?> = _message.asStateFlow()

    val appTheme: StateFlow<DarkThemeConfig> = userPreferencesRepository.appTheme.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DarkThemeConfig.FOLLOW_SYSTEM
    )

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            val result = initializeGitabasesUseCase.execute()

            result.fold(
                onSuccess = {
                    _isLoading.value = false
                    _message.value = null
                },
                onFailure = { exception ->
                    _isLoading.value = false
                    _message.value = stringProvider.getString(
                        R.string.error_prefix,
                        exception.message ?: stringProvider.getString(R.string.error_unknown)
                    )
                }
            )
        }
    }
}
