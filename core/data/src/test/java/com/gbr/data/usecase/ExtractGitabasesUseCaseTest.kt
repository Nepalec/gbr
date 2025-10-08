package com.gbr.data.usecase

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.ByteArrayInputStream
import java.io.File
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ExtractGitabasesUseCaseTest {

    private lateinit var context: Context
    private lateinit var extractGitabasesUseCase: ExtractGitabasesUseCase
    private lateinit var testFolder: File

    @Before
    fun setUp() {
        context = mockk()
        extractGitabasesUseCase = ExtractGitabasesUseCase(context)
        
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
        
        // Mock asset files
        every { mockAssets.list("gitabases") } returns arrayOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db",
            "gitabase_songs_rus.db"
        )
        
        // Mock asset input streams
        every { mockAssets.open("gitabases/gitabase_help_eng.db") } returns ByteArrayInputStream("test data".toByteArray())
        every { mockAssets.open("gitabases/gitabase_help_rus.db") } returns ByteArrayInputStream("test data".toByteArray())
        every { mockAssets.open("gitabases/gitabase_songs_rus.db") } returns ByteArrayInputStream("test data".toByteArray())

        // Execute
        val result = extractGitabasesUseCase.execute(testFolder)

        // Verify
        assertTrue("Should succeed", result.isSuccess)
        val extractedFiles = result.getOrThrow()
        assertEquals("Should extract 3 files", 3, extractedFiles.size)
        
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
        assertTrue("Should contain error message", result.exceptionOrNull()?.message?.contains("Failed to extract") == true)
    }

    @Test
    fun `getAvailableGitabaseFiles should return list of files`() {
        // Mock context resources
        val mockResources = mockk<android.content.res.Resources>()
        val mockAssets = mockk<android.content.res.AssetManager>()
        
        every { context.resources } returns mockResources
        every { mockResources.assets } returns mockAssets
        every { mockAssets.list("gitabases") } returns arrayOf(
            "gitabase_help_eng.db",
            "gitabase_help_rus.db"
        )

        // Execute
        val availableFiles = extractGitabasesUseCase.getAvailableGitabaseFiles()

        // Verify
        assertEquals("Should return 2 files", 2, availableFiles.size)
        assertTrue("Should contain help_eng", availableFiles.contains("gitabase_help_eng.db"))
        assertTrue("Should contain help_rus", availableFiles.contains("gitabase_help_rus.db"))
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
