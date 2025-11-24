package com.gbr.data.test

import com.gbr.data.repository.TextsRepository
import com.gbr.data.usecase.LoadBookDetailUseCase
import com.gbr.model.gitabase.GitabaseID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Base class for ViewModel integration tests.
 * Provides common setup and teardown for all ViewModel tests.
 *
 * Supports both Robolectric and instrumented tests.
 * Subclasses should specify their own @RunWith annotation:
 * - For Robolectric: @RunWith(RobolectricTestRunner::class) with @Config
 * - For instrumented: @RunWith(AndroidJUnit4::class)
 *
 * Usage:
 *
 * @RunWith(RobolectricTestRunner::class)
 * @Config(sdk = [28], manifest = Config.NONE)
 * class MyViewModelTest : BaseViewModelTest() {
 *     @Test
 *     fun testSomething() = runTest {
 *         // Initialize gitabases before use
 *         initializeGitabases()
 *         // Use textsRepository, loadBookDetailUseCase, testGitabaseId
 *     }
 * }
 *  */
abstract class BaseViewModelTest {

    companion object {
        @Volatile
        private var fixtureInstance: TestDatabaseFixture? = null

        @JvmStatic
        fun getOrCreateFixture(): TestDatabaseFixture {
            return fixtureInstance ?: synchronized(this) {
                fixtureInstance ?: TestDatabaseFixture.getInstance().also {
                    fixtureInstance = it
                }
            }
        }

        @JvmStatic
        fun cleanupFixture() {
            synchronized(this) {
                fixtureInstance?.let {
                    TestDatabaseFixture.cleanup()
                    fixtureInstance = null
                }
            }
        }
    }

    // Initialize fixture in @Before (works with both Robolectric and instrumented tests)
    @Before
    fun setUp() {
        // Explicitly initialize fixture to ensure Robolectric is ready
        // This ensures ApplicationProvider.getApplicationContext() works correctly
        getOrCreateFixture()
    }

    @After
    fun tearDown() {
        // Don't cleanup here - let it be cleaned up after all tests
        // Cleanup happens in companion object cleanup method
    }

    // Convenience properties for subclasses
    protected val fixture: TestDatabaseFixture
        get() = getOrCreateFixture()

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
     *
     * @Test
     * fun myTest() = runTest {
     *     initializeGitabases()
     *     // Now gitabases are available
     * }
     *      */
    protected suspend fun initializeGitabases() {
        fixture.initializeGitabases()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

