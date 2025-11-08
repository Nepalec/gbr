package com.gbr.data.repository

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

/**
 * Robolectric test that uses the REAL NetworkModule with simulated Android framework.
 * This test runs on JVM with Robolectric and has access to:
 * - Simulated ApplicationContext (for ImageLoader)
 * - Real HttpLoggingInterceptor (for network logging)
 * - Real BuildConfig.BASE_URL (for real API endpoint)
 * - All production Hilt modules
 * - Real network calls to the internet
 */
@RunWith(RobolectricTestRunner::class)
@Config(
    sdk = [28], // Android 9
    application = dagger.hilt.android.testing.HiltTestApplication::class
)
@HiltAndroidTest
class GitabasesDescRepositoryRobolectricTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: GitabasesDescRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun repository_with_real_NetworkModule_should_return_texts_databases() = runTest {
        val result = repository.getGitabasesDesc()

        // Then - Should handle 404 error gracefully
        assertEquals("Should have successful response", 1, result.success)
        assertTrue("Should have texts gitabase", result.gitabases.firstOrNull { it.gbalias == "texts" } != null)
    }
}
