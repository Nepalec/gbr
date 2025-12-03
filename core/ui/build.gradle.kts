plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.gbr.ui"

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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt dependencies
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // Compose Material3 for SnackbarDuration
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
}

