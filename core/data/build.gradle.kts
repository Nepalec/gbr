plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.gbr.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    testOptions {
        // Grant permissions automatically for tests
        // execution 'ANDROIDX_TEST_ORCHESTRATOR'

        // Test-specific configurations
        unitTests {
            isIncludeAndroidResources = true
        }

        // Grant permissions for tests
        animationsDisabled = true
    }

    // Enable testFixtures to share test utilities with other modules
    testFixtures {
        enable = true
    }
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(projects.core.datasource)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt dependencies
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager for background downloads
    implementation(libs.androidx.work.runtime.ktx)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)
    testImplementation(libs.hilt.android.testing)

    // Android test dependencies
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    // Hilt test dependencies
    kspAndroidTest(libs.hilt.compiler)
    kspTest(libs.hilt.compiler)

    // Test fixtures dependencies - needed for testFixtures code
    // testFixtures automatically inherits from api and implementation
    // but we need to explicitly add test dependencies
    testFixturesImplementation(libs.androidx.test.ext.junit)
    testFixturesImplementation(libs.androidx.test.runner)
    testFixturesImplementation(libs.androidx.test.core)
    testFixturesImplementation(libs.kotlinx.coroutines.test)
    // Add Kotlin stdlib for testFixtures (required for AGP 8.5.1+)
    testFixturesCompileOnly(libs.kotlin.stdlib)
    // Add core.common for StringProvider used in testFixtures
    testFixturesImplementation(projects.core.common)
    // Also need to expose api dependencies to testFixtures consumers
    // This ensures that testFixtures consumers can see the model classes
    testFixturesApi(projects.core.model)
}