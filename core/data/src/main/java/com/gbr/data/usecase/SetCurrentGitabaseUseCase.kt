package com.gbr.data.usecase

import android.util.Log
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.UserPreferencesRepository
import com.gbr.model.gitabase.GitabaseID
import javax.inject.Inject

/**
 * Use case for setting the current gitabase and persisting the user's selection.
 * Orchestrates both the gitabase repository state and user preferences persistence.
 */
class SetCurrentGitabaseUseCase @Inject constructor(
    private val gitabasesRepository: GitabasesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {

    companion object {
        private const val TAG = "SetCurrentGitabaseUseCase"
    }

    /**
     * Sets the current gitabase in the repository and saves the user's selection.
     * If preference saving fails, the gitabase selection still works.
     *
     * @param gitabaseId The GitabaseID to set as current
     */
    suspend fun execute(gitabaseId: GitabaseID) {
        // Update repository state
        gitabasesRepository.setCurrentGitabase(gitabaseId)
        
        // Persist user preference
        try {
            userPreferencesRepository.setLastUsedGitabase(gitabaseId)
        } catch (e: Exception) {
            // Log but don't fail - gitabase selection still works
            Log.e(TAG, "Failed to save gitabase preference, but selection will still work", e)
        }
    }
}
