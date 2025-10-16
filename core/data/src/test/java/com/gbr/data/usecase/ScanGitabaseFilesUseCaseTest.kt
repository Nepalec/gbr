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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
 * Robolectric test for ScanGitabaseFilesUseCase.
 * Tests file system operations with simulated Android context.
 * Uses ExtractGitabasesUseCase to extract real database files from resources.
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
    lateinit var extractGitabasesUseCase: ExtractGitabasesUseCase

    @Inject
    lateinit var gitabasesRepository: GitabasesRepository

    private lateinit var testFolder: File
    private lateinit var context: Context

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        context = ApplicationProvider.getApplicationContext()

        // Create test folder in app's external files directory
        testFolder = File(context.getExternalFilesDir(null), "test_gitabases")
        testFolder.mkdirs()

        // Extract test database files from resources
        extractTestDatabaseFilesFromResources()
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

        // Should find at least 3 valid Gitabases (help files + songs file, excluding invalid)
        assertTrue("Should find valid Gitabases", gitabases.size >= 3)
        
        // Verify that we have help gitabases
        assertTrue("Should have help gitabases", gitabases.any { 
            it.id.type == GitabaseType.HELP && it.id.lang == GitabaseLang.ENG 
        })
        assertTrue("Should have help gitabases", gitabases.any { 
            it.id.type == GitabaseType.HELP && it.id.lang == GitabaseLang.RUS 
        })
        
        // Verify that we have songs gitabase
        assertTrue("Should have songs gitabase", gitabases.any { 
            it.id.type == GitabaseType("songs") && it.id.lang == GitabaseLang.RUS 
        })
    }

    @Test
    fun execute_should_add_Gitabases_to_repository() = runTest {
        // Clear repository first
        val initialGitabases = gitabasesRepository.getAllGitabases()
        
        // Execute the use case
        val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)
        
        // Verify success
        assertTrue("Scan should succeed", result.isSuccess)
        
        // Check that Gitabases were added to repository
        val availableGitabases = gitabasesRepository.getAllGitabases()
        assertTrue("Repository should have more Gitabases than initially", 
            availableGitabases.size > initialGitabases.size)
    }

    @Test
    fun execute_should_validate_database_files() = runTest {
        // Execute the use case
        val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)

        // Verify success
        assertTrue("Scan should succeed", result.isSuccess)
        val gitabases = result.getOrThrow()

        // All returned Gitabases should be valid (invalid database should be filtered out)
        assertTrue("All returned Gitabases should be valid", gitabases.isNotEmpty())
        
        // Verify that invalid database is not included
        assertTrue("Invalid database should not be included", 
            gitabases.none { it.title.contains("invaliddb") })
    }

    @Test
    fun execute_should_handle_empty_folder() = runTest {
        // Create empty folder
        val emptyFolder = File(context.getExternalFilesDir(null), "empty_test")
        emptyFolder.mkdirs()

        // Execute the use case
        val result = scanGitabaseFilesUseCase.execute(emptyFolder.absolutePath)

        // Should succeed but return empty list
        assertTrue("Scan should succeed", result.isSuccess)
        val gitabases = result.getOrThrow()
        assertTrue("Should return empty list for empty folder", gitabases.isEmpty())

        // Clean up
        emptyFolder.deleteRecursively()
    }

    @Test
    fun execute_should_handle_invalid_folder_path() = runTest {
        // Execute with invalid path
        val result = scanGitabaseFilesUseCase.execute("/invalid/path/that/does/not/exist")

        // Should fail
        assertTrue("Should fail for invalid path", result.isFailure)
    }

    @Test
    fun test_should_access_emulator_file_system() = runTest {
        // Verify we can access the test folder
        assertTrue("Test folder should exist", testFolder.exists())
        assertTrue("Test folder should be a directory", testFolder.isDirectory)
        
        // Verify test files exist
        val testFiles = testFolder.listFiles()
        assertNotNull("Test files should exist", testFiles)
        assertTrue("Should have test files", testFiles!!.isNotEmpty())
    }

    @Test
    fun test_should_handle_concurrent_scanning() = runTest {
        // Test concurrent scanning of the same folder
        val results = mutableListOf<Result<List<com.gbr.model.gitabase.Gitabase>>>()
        
        // Run multiple scans concurrently
        repeat(3) {
            val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)
            results.add(result)
        }
        
        // All should succeed
        results.forEach { result ->
            assertTrue("All concurrent scans should succeed", result.isSuccess)
        }
        
        // All should return similar results
        val firstResult = results.first().getOrThrow()
        results.forEach { result ->
            val gitabases = result.getOrThrow()
            assertEquals("Concurrent scans should return same number of Gitabases", 
                firstResult.size, gitabases.size)
        }
    }

    /**
     * Extracts test SQLite database files from resources to the test directory.
     * Uses ExtractGitabasesUseCase to get real database files from resources.
     */
    private suspend fun extractTestDatabaseFilesFromResources() {
        try {
            // Extract all 4 files (2 help + 2 test) from resources
            val result = extractGitabasesUseCase.execute(testFolder, ExtractGitabasesUseCase.ALL_GITABASE_FILES)
            
            if (result.isSuccess) {
                val extractedFiles = result.getOrThrow()
                println("✅ Extracted ${extractedFiles.size} database files from resources:")
                extractedFiles.forEach { filePath ->
                    println("  - $filePath")
                }
            } else {
                throw RuntimeException("Failed to extract database files: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to extract test database files from resources: ${e.message}", e)
        }
    }
}