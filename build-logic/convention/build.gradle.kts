plugins {
    `kotlin-dsl` // use gradlePlugin to register the plugin we created, which helps gradle to discover our plugins
}

group = "com.gbr.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        create("androidApplicationCompose") {
            id = "com.gbr.convention.application.compose"
            implementationClass = "com.gbr.convention.AndroidApplicationComposeConventionPlugin"
        }

        create("androidApplication") {
            id = "com.gbr.convention.application"
            implementationClass = "com.gbr.convention.AndroidApplicationConventionPlugin"
        }

        create("androidLibrary") {
            id = "com.gbr.convention.library"
            implementationClass = "com.gbr.convention.AndroidLibraryConventionPlugin"
        }

        create("androidLibraryCompose") {
            id = "com.gbr.convention.library.compose"
            implementationClass = "com.gbr.convention.AndroidLibraryComposeConventionPlugin"
        }

        create("hilt") {
            id = "com.gbr.convention.hilt"
            implementationClass = "com.gbr.convention.AndroidHiltConventionPlugin"
        }

        create("jvmLibrary") {
            id = "com.gbr.convention.jvm.library"
            implementationClass = "com.gbr.convention.JvmLibraryConventionPlugin"
        }
    }
}