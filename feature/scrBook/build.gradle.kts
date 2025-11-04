plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.library.compose)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.org.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.gbr.scrbook"

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
    implementation(projects.core.network)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)
    
    // Material Icons Extended (version managed by Compose BOM)
    implementation("androidx.compose.material:material-icons-extended")
    
    // Coil for image loading
    implementation(libs.coil.kt.compose)
}
