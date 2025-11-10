package com.gbr.data.usecase

import android.content.Context
import com.gbr.common.strings.StringProvider
import com.gbr.data.usecase.ExtractGitabasesUseCase.Companion.ALL_GITABASE_FILES
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.File

class ExtractGitabasesUseCaseTest {

    private lateinit var context: Context
    private lateinit var stringProvider: StringProvider
    private lateinit var extractGitabasesUseCase: ExtractGitabasesUseCase
    private lateinit var testFolder: File

    @Before
    fun setUp() {
        context = mockk()
        stringProvider = mockk()
        every { stringProvider.getString(any()) } returns "Error message"
        every { stringProvider.getString(any(), *anyVararg<Any>()) } returns "Error message"
        extractGitabasesUseCase = ExtractGitabasesUseCase(context, stringProvider)

        // Create a temporary test folder
        testFolder = File.createTempFile("test_gitabases", "")
        testFolder.delete()
        testFolder.mkdirs()
    }

    @Test
    fun `execute should extract files successfully`() = runTest {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()

        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets

        // Mock asset files for both directories
        every { mockAssets.list("gitabases") } returns arrayOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db"
        )
        every { mockAssets.list("test_gitabases") } returns arrayOf(
            "gitabase_songs_rus.db",
            "gitabase_invaliddb_eng.db"
        )

        // Mock asset input streams for all 4 files
        every { mockAssets.open("gitabases/gitabase_help_eng.db") } returns ByteArrayInputStream("test data".toByteArray())
        every { mockAssets.open("gitabases/gitabase_help_rus.db") } returns ByteArrayInputStream("test data".toByteArray())
        every { mockAssets.open("test_gitabases/gitabase_songs_rus.db") } returns ByteArrayInputStream("test data".toByteArray())
        every { mockAssets.open("test_gitabases/gitabase_invaliddb_eng.db") } returns ByteArrayInputStream("test data".toByteArray())

        // Execute
        val result = extractGitabasesUseCase.execute(testFolder, ALL_GITABASE_FILES)

        // Verify
        assertTrue("Should succeed", result.isSuccess)
        val extractedFiles = result.getOrThrow()
        assertEquals("Should extract 4 files (2 help + 2 test)", 4, extractedFiles.size)

        // Verify files were created
        extractedFiles.forEach { filePath ->
            val file = File(filePath)
            assertTrue("File should exist: $filePath", file.exists())
        }
    }

    @Test
    fun `execute should handle missing files gracefully`() = runTest {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()

        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets

        // Mock asset files (empty list)
        every { mockAssets.list("gitabases") } returns emptyArray()

        // Mock asset input streams to throw exception
        every { mockAssets.open("gitabases/gitabase_help_eng.db") } throws Exception("File not found")

        // Execute
        val result = extractGitabasesUseCase.execute(testFolder)

        // Verify
        assertFalse("Should fail", result.isSuccess)
        assertTrue(
            "Should contain error message",
            result.exceptionOrNull()?.message?.contains("Failed to extract") == true
        )
    }

    @Test
    fun `getAvailableGitabaseFiles should return list of files`() {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()

        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets

        // Mock both directories
        every { mockAssets.list("gitabases") } returns arrayOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db"
        )
        every { mockAssets.list("test_gitabases") } returns arrayOf(
            "gitabase_songs_rus.db",
            "gitabase_invaliddb_eng.db"
        )

        // Execute
        val availableFiles = extractGitabasesUseCase.getAvailableGitabaseFiles()

        // Verify
        assertEquals("Should return 4 files (2 help + 2 test)", 4, availableFiles.size)
        assertTrue("Should contain help_eng", availableFiles.contains("gitabase_help_eng.db"))
        assertTrue("Should contain help_rus", availableFiles.contains("gitabase_help_rus.db"))
        assertTrue("Should contain songs_rus", availableFiles.contains("gitabase_songs_rus.db"))
        assertTrue("Should contain invaliddb_eng", availableFiles.contains("gitabase_invaliddb_eng.db"))
    }

    @Test
    fun `isGitabaseFileAvailable should return true for existing file`() {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()

        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets
        every { mockAssets.open("gitabases/gitabase_help_eng.db") } returns ByteArrayInputStream("test data".toByteArray())

        // Execute
        val isAvailable = extractGitabasesUseCase.isGitabaseFileAvailable("gitabase_help_eng.db")

        // Verify
        assertTrue("File should be available", isAvailable)
    }

    @Test
    fun `isGitabaseFileAvailable should return false for non-existing file`() {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()

        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets
        every { mockAssets.open("gitabases/nonexistent.db") } throws Exception("File not found")

        // Execute
        val isAvailable = extractGitabasesUseCase.isGitabaseFileAvailable("nonexistent.db")

        // Verify
        assertFalse("File should not be available", isAvailable)
    }
}
