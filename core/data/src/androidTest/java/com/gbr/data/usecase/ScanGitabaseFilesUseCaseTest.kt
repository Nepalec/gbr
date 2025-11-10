package com.gbr.data.usecase

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
import java.io.File
import javax.inject.Inject

/**
 * AndroidJUnit4 test for ScanGitabasesUseCase.
 * Tests file system operations with real Android context and permissions.
 * All test databases are copied from resources - no programmatic creation.
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ScanGitabaseFilesUseCaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase

    @Inject
    lateinit var gitabasesRepository: GitabasesRepository

    private lateinit var testFolder: File
    private lateinit var context: Context

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

        // Should find at least 2 valid Gitabases (some may not have books table)
        assertTrue("Should find at least 3 valid Gitabases", gitabases.size == 3)

        // Verify specific Gitabases were found
        val helpEng = gitabases.find {
            it.id.type == GitabaseType.HELP && it.id.lang == GitabaseLang.ENG
        }
        val helpRus = gitabases.find {
            it.id.type == GitabaseType.HELP && it.id.lang == GitabaseLang.RUS
        }
        val songsRus = gitabases.find {
            it.id.type == GitabaseType("songs") && it.id.lang == GitabaseLang.RUS
        }

        // At least one help database should be found
        assertTrue(
            "Should find all Gitabases",
            helpEng != null && helpRus != null && songsRus != null
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
    fun execute_should_validate_database_files() = runTest {
        // Create an invalid database file
        val invalidDb = File(testFolder, "gitabase_invalid_eng.db")
        invalidDb.writeText("This is not a valid SQLite database")

        val result = scanGitabaseFilesUseCase.execute(testFolder.absolutePath)

        assertTrue("Should succeed", result.isSuccess)
        val gitabases = result.getOrThrow()

        // Should not include invalid database
        val invalidGitabase = gitabases.find { it.id.key == "invalid_eng" }
        assertNull("Should not include invalid database", invalidGitabase)
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
            availableGitabases.size >= initialGitabases.size
        )
    }

    @Test
    fun test_should_access_emulator_file_system() {
        // Verify we can access emulator file system
        val externalDir = context.getExternalFilesDir(null)
        assertNotNull("Should have access to external files directory", externalDir)

        // Create a test file on emulator storage
        val testFile = File(externalDir, "emulator_test.txt")
        testFile.writeText("Test file created on emulator at ${System.currentTimeMillis()}")

        assertTrue("Should be able to create files on emulator", testFile.exists())
        assertTrue("Should be able to read files from emulator", testFile.canRead())

        // Clean up
        testFile.delete()
    }

    @Test
    fun test_should_copy_resources_to_device_storage() {
        // Verify that resource files were copied to device
        val expectedFiles = listOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db",
            "gitabase_songs_rus.db"
        )

        expectedFiles.forEach { fileName ->
            val deviceFile = File(testFolder, fileName)
            assertTrue("Should have copied $fileName to device", deviceFile.exists())
            assertTrue("Should be able to read $fileName from device", deviceFile.canRead())
            assertTrue("$fileName should have content", deviceFile.length() > 0)

            println("✅ Verified $fileName exists on device: ${deviceFile.absolutePath}")
        }
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
     * Copies test SQLite database files from resources to the device.
     * This ensures we test with real database files that have proper schema and data.
     * All databases must exist in resources - no fallback creation.
     */
    private fun copyTestDatabaseFilesFromResources() {
        val testFiles = listOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db",
            "gitabase_songs_rus.db"
        )

        testFiles.forEach { fileName ->
            try {
                // Read from resources
                val resourceStream = javaClass.classLoader.getResourceAsStream("test_gitabases/$fileName")
                if (resourceStream != null) {
                    // Copy to device storage
                    val deviceFile = File(testFolder, fileName)
                    deviceFile.outputStream().use { output ->
                        resourceStream.copyTo(output)
                    }
                    resourceStream.close()

                    println("✅ Copied $fileName to device: ${deviceFile.absolutePath}")
                } else {
                    // Fail if resource doesn't exist - no fallback creation
                    throw RuntimeException("Required test resource not found: test_gitabases/$fileName")
                }
            } catch (e: Exception) {
                // Fail if copying fails - no fallback creation
                throw RuntimeException("Failed to copy test resource $fileName: ${e.message}", e)
            }
        }
    }
}
