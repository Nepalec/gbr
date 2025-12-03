plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.library.compose)
    alias(libs.plugins.gbr.android.hilt)
    id("kotlin-parcelize")
}

android {
    namespace = "com.gbr.scrDownloader"

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
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.model)
    implementation(projects.core.data)
    implementation(projects.core.datastore)
    implementation(projects.core.ui)
    implementation(projects.core.work)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.work.runtime.ktx)

    // Compose UI Tooling for previews
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.tooling.preview)
}