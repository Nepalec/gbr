package com.gbr.network

import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

// core/network/src/androidTest/java/com/gbr/network/RetrofitShopNetworkTest.kt
@HiltAndroidTest
class RetrofitShopNetworkTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    internal lateinit var shopDataSource: IShopDataSource

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun getShopRoot_hitsRealApi() = runTest {
        val resp = shopDataSource.getShopContents("en")

        // Example assertion depending on your model
        assertTrue(resp.authors.isNotEmpty())
    }
}