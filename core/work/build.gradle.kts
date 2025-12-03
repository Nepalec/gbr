plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.gbr.work"

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
    api(projects.core.model)
    
    implementation(projects.core.data)
    implementation(projects.core.datasource)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Hilt dependencies
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    
    // Assisted injection for @AssistedInject in Hilt Workers
    implementation("com.squareup.inject:assisted-inject-annotations-dagger2:0.8.1")
    ksp("com.squareup.inject:assisted-inject-processor-dagger2:0.8.1")

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}

