plugins {
    alias(libs.plugins.gbr.android.application.compose)
    alias(libs.plugins.gbr.android.hilt)
}

android {
    namespace = "com.gbr"

    defaultConfig {
        applicationId = "com.gbr"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(projects.core.designsystem)
    implementation(projects.feature.tabbooks)
    implementation(projects.feature.tabreading)
    implementation(projects.feature.tabnotes)
    implementation(projects.feature.tabprofile)
    implementation(projects.feature.settings)
    implementation(libs.androidx.tracing.ktx)

    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit4)
}
