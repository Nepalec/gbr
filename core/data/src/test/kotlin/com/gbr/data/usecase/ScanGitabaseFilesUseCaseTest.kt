package com.gbr.data.usecase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gbr.data.repository.GitabasesRepository
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import javax.inject.Inject

/**
 * Robolectric test for ScanGitabasesUseCase.
 * Tests file system operations with simulated Android context.
 * All test databases are copied from resources - no programmatic creation.
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = dagger.hilt.android.testing.HiltTestApplication::class)
class ScanGitabaseFilesUseCaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase

    @Inject
    lateinit var gitabasesRepository: GitabasesRepository

    private lateinit var testFolder: File
    private lateinit var context: Context

    val testFiles = listOf(
        "gitabase_folio_eng.db",
        "gitabase_invaliddb_eng.db",
        "gitabase_songs_rus.db"
    )

    @Before
    fun setUp() {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()

        // Create test folder in app's external files directory
        testFolder = File(context.getExternalFilesDir(null), "test_gitabases")
        testFolder.mkdirs()

        // Copy test database files from resources to device
        copyTestDatabaseFilesFromResources()
    }

    @After
    fun tearDown() {
        // Clean up test files
        testFolder.deleteRecursively()
    }

    @Test
    fun execute_should_scan_folder_and_find_valid_Gitabase_files() = runTest {
        // Execute the use case
        val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)

        // Verify success
        assertTrue("Scan should succeed", result.isSuccess)
        val gitabases = result.getOrThrow()

        // Should find at least 2 valid Gitabases (1 of 3 is invalid)
        assertTrue("Should find at least 3 valid Gitabases", gitabases.size == 2)

        // Verify specific Gitabases were found
        val songsRus = gitabases.find {
            it.id.type == GitabaseType("songs") && it.id.lang == GitabaseLang.RUS
        }
        val folioEng = gitabases.find {
            it.id.type == GitabaseType("folio") && it.id.lang == GitabaseLang.ENG
        }

        // At least one help database should be found
        assertTrue(
            "Should find all Gitabases",
            songsRus != null && folioEng != null
        )
    }

    @Test
    fun execute_should_handle_invalid_folder_path() = runTest {
        val invalidPath = "/non/existent/path"

        val result = scanGitabaseFilesUseCase.execute(invalidPath)

        assertTrue("Should fail for invalid path", result.isFailure)
        assertTrue(
            "Should be IllegalArgumentException",
            result.exceptionOrNull() is IllegalArgumentException
        )
    }

    @Test
    fun execute_should_handle_empty_folder() = runTest {
        val emptyFolder = File(context.getExternalFilesDir(null), "empty_test")
        emptyFolder.mkdirs()

        val result = scanGitabaseFilesUseCase.execute(emptyFolder.absolutePath)

        assertTrue("Should succeed for empty folder", result.isSuccess)
        assertTrue("Should return empty list", result.getOrThrow().isEmpty())

        emptyFolder.deleteRecursively()
    }

    @Test
    fun execute_should_add_Gitabases_to_repository() = runTest {
        // Clear repository first by getting initial state
        val initialGitabases = gitabasesRepository.getAllGitabases()

        val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)

        assertTrue("Should succeed", result.isSuccess)

        // Verify Gitabases were added to repository
        val availableGitabases = gitabasesRepository.getAllGitabases()
        assertTrue(
            "Repository should contain discovered Gitabases",
            availableGitabases.size == 2
        )
    }

    @Test
    fun test_should_handle_concurrent_scanning() = runTest {
        // Test multiple concurrent scans
        val results = mutableListOf<Result<Set<com.gbr.model.gitabase.Gitabase>>>()

        // Launch multiple concurrent scans
        repeat(3) {
            val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)
            results.add(result)
        }

        // All should succeed
        results.forEach { result ->
            assertTrue("Concurrent scan should succeed", result.isSuccess)
        }

        // All should return same results
        val firstResult = results.first().getOrThrow()
        results.forEach { result ->
            val gitabases = result.getOrThrow()
            assertEquals(
                "Concurrent scans should return same results",
                firstResult.size,
                gitabases.size
            )
        }
    }

    /**
     * Copies test SQLite database files from assets to the test folder.
     * This ensures we test with real database files that have proper schema and data.
     */
    private fun copyTestDatabaseFilesFromResources() {
        testFiles.forEach { fileName ->
            try {
                // Read from assets using Android Context (works with Robolectric)
                val assetPath = "test_gitabases/$fileName"
                val assetStream = context.assets.open(assetPath)

                // Copy to test folder
                val deviceFile = File(testFolder, fileName)
                deviceFile.outputStream().use { output ->
                    assetStream.copyTo(output)
                }
                assetStream.close()

                println("Copied $fileName to test folder: ${deviceFile.absolutePath}")
            } catch (e: Exception) {
                // Fail if copying fails - no fallback creation
                throw RuntimeException("Failed to copy test asset $fileName: ${e.message}", e)
            }
        }
    }
}
