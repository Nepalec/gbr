plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.org.jetbrains.kotlin.kapt)
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
}

dependencies {
    api(projects.core.model)
    implementation(projects.core.network)
    implementation(projects.core.datastore)
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt dependencies
    implementation(libs.dagger.hilt)
    kapt(libs.dagger.hilt.compiler)

    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

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
    kaptAndroidTest(libs.hilt.compiler)
    kaptTest(libs.hilt.compiler)
}

// Fix for KAPT with Java 17+
// tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//     kotlinOptions {
//         jvmTarget = "17"
//     }
// }
//
// kapt {
//     javacOptions {
//         option("-Xmaxerrs", 500)
//     }
// }
