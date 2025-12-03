plugins {
    alias(libs.plugins.gbr.android.library)
    alias(libs.plugins.gbr.android.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.dagger.hilt)
}

android {
    namespace = "com.gbr.datasource"

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
    
    implementation(projects.core.network)
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // OkHttp for RemoteFileDataSourceImpl (needs OkHttpClient type)
    implementation(libs.okhttp.core)

    // Hilt dependencies
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    // Test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
}

