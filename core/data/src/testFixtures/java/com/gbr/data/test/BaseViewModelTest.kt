package com.gbr.data.test

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gbr.data.repository.TextsRepository
import com.gbr.data.usecase.LoadBookDetailUseCase
import com.gbr.model.gitabase.GitabaseID
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.runner.RunWith

/**
 * Base class for ViewModel integration tests.
 * Provides common setup and teardown for all ViewModel tests.
 * 
 * Usage:
 * ```kotlin
 * class MyViewModelTest : BaseViewModelTest() {
 *     @Test
 *     fun testSomething() = runTest {
 *         // Initialize gitabases before use
 *         initializeGitabases()
 *         // Use textsRepository, loadBookDetailUseCase, testGitabaseId
 *     }
 * }
 * ```
 */
@RunWith(AndroidJUnit4::class)
abstract class BaseViewModelTest {
    
    companion object {
        @JvmStatic
        lateinit var fixture: TestDatabaseFixture
        
        @BeforeClass
        @JvmStatic
        fun setupClass() {
            fixture = TestDatabaseFixture.getInstance()
        }
        
        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            TestDatabaseFixture.cleanup()
        }
    }
    
    // Convenience properties for subclasses
    protected val textsRepository: TextsRepository
        get() = fixture.textsRepository
    
    protected val loadBookDetailUseCase: LoadBookDetailUseCase
        get() = fixture.loadBookDetailUseCase
    
    protected val testGitabaseId: GitabaseID
        get() = fixture.getTestGitabaseId()

    /**
     * Initializes gitabases by scanning the test folder.
     * Call this in your test's runTest block before accessing gitabases.
     * 
     * Example:
     * ```kotlin
     * @Test
     * fun myTest() = runTest {
     *     initializeGitabases()
     *     // Now gitabases are available
     * }
     * ```
     */
    protected suspend fun initializeGitabases() {
        fixture.initializeGitabases()
    }
}

