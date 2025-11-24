package com.gbr.data.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.gbr.common.strings.StringProvider
import com.gbr.data.database.GitabaseDatabaseManager
import com.gbr.data.repository.GitabasesRepository
import com.gbr.data.repository.GitabasesRepositoryImpl
import com.gbr.data.repository.ImageFilesRepository
import com.gbr.data.repository.ImageFilesRepositoryImpl
import com.gbr.data.repository.TextsRepository
import com.gbr.data.repository.TextsRepositoryImpl
import com.gbr.data.usecase.LoadBookDetailUseCase
import com.gbr.data.usecase.ScanGitabaseFilesUseCase
import com.gbr.model.gitabase.GitabaseID
import com.gbr.model.gitabase.GitabaseLang
import com.gbr.model.gitabase.GitabaseType
import java.io.File

/**
 * Test fixture that sets up real database infrastructure for integration tests.
 * Reusable across all ViewModel tests.
 *
 * This fixture:
 * - Copies test gitabase files from assets to a test folder
 * - Creates real DatabaseManager, Repositories, and UseCases
 * - Provides singleton access to avoid duplicating setup code
 */
class TestDatabaseFixture private constructor() {

    lateinit var context: Context
    lateinit var testFolderPath: String
    lateinit var databaseManager: GitabaseDatabaseManager
    lateinit var textsRepository: TextsRepository
    lateinit var imageFilesRepository: ImageFilesRepository
    lateinit var loadBookDetailUseCase: LoadBookDetailUseCase
    lateinit var gitabasesRepository: GitabasesRepository
    lateinit var scanGitabaseFilesUseCase: ScanGitabaseFilesUseCase

    // Mock dependencies
    lateinit var stringProvider: StringProvider

    companion object {
        @Volatile
        private var INSTANCE: TestDatabaseFixture? = null

        /**
         * Gets or creates the singleton test fixture.
         * Call this once in @BeforeClass or setUp().
         */
        fun getInstance(): TestDatabaseFixture {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TestDatabaseFixture().apply {
                    setup()
                    INSTANCE = this
                }
            }
        }

        /**
         * Cleans up the fixture.
         * Call this in @AfterClass or tearDown().
         */
        fun cleanup() {
            INSTANCE?.tearDown()
            INSTANCE = null
        }
    }

    private fun setup() {
        context = ApplicationProvider.getApplicationContext()

        // 1. Setup test databases from assets
        testFolderPath = setupTestDatabases(context)

        // 2. Create real GitabasesRepository
        gitabasesRepository = GitabasesRepositoryImpl()

        // 3. Create mock StringProvider (can be replaced with real one if needed)
        stringProvider = createMockStringProvider()

        // 4. Create real DatabaseManager
        databaseManager = GitabaseDatabaseManager(
            context = context,
            gitabaseFolderPath = testFolderPath,
            maxCacheSize = 3
        )

        // 5. Create real repositories
        textsRepository = TextsRepositoryImpl(
            context = context,
            gitabasesRepository = gitabasesRepository,
            databaseManager = databaseManager,
            stringProvider = stringProvider
        )

        imageFilesRepository = ImageFilesRepositoryImpl(context)

        // 6. Create real UseCase
        loadBookDetailUseCase = LoadBookDetailUseCase(
            textsRepository = textsRepository,
            imageFilesRepository = imageFilesRepository,
            stringProvider = stringProvider
        )

        // 7. Create real ScanGitabaseFilesUseCase
        scanGitabaseFilesUseCase = ScanGitabaseFilesUseCase(gitabasesRepository,
            null, stringProvider)

    }

    /**
     * Initializes gitabases by scanning the test folder.
     * Must be called in a coroutine context (e.g., runTest block).
     *
     * This method:
     * - Scans the test folder for .db files
     * - Validates and registers them in GitabasesRepository
     * - Should be called before accessing gitabases in tests
     */
    suspend fun initializeGitabases() {
        val result = scanGitabaseFilesUseCase.execute(testFolderPath)
        if (result.isFailure) {
            throw RuntimeException("Failed to initialize gitabases: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
        }
    }

    private fun tearDown() {
        databaseManager.closeAll()
        cleanupTestDatabases(context)
    }

    /**
     * Helper to get a test GitabaseID (songs_rus from test_gitabases)
     */
    fun getTestGitabaseId(): GitabaseID {
        return GitabaseID(
            type = GitabaseType.HELP,
            lang = GitabaseLang.ENG
        )
    }

    /**
     * Copies test gitabase files from assets to a test folder.
     */
    private fun setupTestDatabases(context: Context): String {
        val testFolder = File(context.getExternalFilesDir(null), "test_gitabases_integration")
        if (testFolder.exists()) {
            testFolder.deleteRecursively()
        }
        testFolder.mkdirs()

        val testFiles = listOf(
            "gitabase_folio_eng.db",
            "gitabase_help_eng.db",
           // "gitabase_help_rus.db"
        )

        testFiles.forEach { fileName ->
            try {
                val assetPath = when {
                    fileName.startsWith("gitabase_folio") ||
                    fileName.startsWith("gitabase_invalid") -> "test_gitabases/$fileName"
                    else -> "gitabases/$fileName"
                }

                val inputStream = context.assets.open(assetPath)
                val outputFile = File(testFolder, fileName)

                outputFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }
                inputStream.close()

                println("✅ Copied $fileName to ${outputFile.absolutePath}")
            } catch (e: Exception) {
                throw RuntimeException("Failed to copy test database $fileName: ${e.message}", e)
            }
        }

        return testFolder.absolutePath
    }

    private fun cleanupTestDatabases(context: Context) {
        val testFolder = File(context.getExternalFilesDir(null), "test_gitabases_integration")
        if (testFolder.exists()) {
            testFolder.deleteRecursively()
        }
    }

    /**
     * Creates a mock StringProvider for testing.
     * Can be replaced with real implementation if needed.
     */
    private fun createMockStringProvider(): StringProvider {
        return object : StringProvider {
            override fun getString(resId: Int): String {
                return "String_$resId"
            }

            override fun getString(resId: Int, vararg formatArgs: Any): String {
                return "String_$resId"
            }
        }
    }
}

