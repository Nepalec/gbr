plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.library.compose)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.gbr.scrchapter"

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
    packaging {
        resources {
            excludes += "/META-INF/LICENSE*"
            excludes += "/META-INF/NOTICE*"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.network)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    // Coil for image loading
    implementation(libs.coil.kt.compose)

    testImplementation(testFixtures(projects.core.data)) // For Robolectric tests

    // Unit test dependencies (Robolectric)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk) // Use regular mockk for unit tests
    testImplementation(libs.kotlinx.coroutines.test)
}