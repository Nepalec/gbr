package com.gbr.convention

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KaptExtension

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        // Ensure Kotlin toolchain = Java 17
        extensions.configure<KotlinProjectExtension> {
            jvmToolchain(17)
        }

        // Apply Hilt + KAPT (Hilt uses KAPT, not KSP)
        pluginManager.apply("com.google.dagger.hilt.android")
        pluginManager.apply("org.jetbrains.kotlin.kapt")

        // Optional but useful KAPT config
        extensions.configure<KaptExtension> {
            correctErrorTypes = true
        }

        val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

        dependencies {
            // Hilt runtime
            add("implementation", libs.findLibrary("dagger-hilt").get())

            // Hilt compiler for main
            add("kapt", libs.findLibrary("dagger-hilt-compiler").get())

            // ---- Instrumented tests ----
            add("androidTestImplementation", libs.findLibrary("dagger-hilt-testing").get())
            add("kaptAndroidTest", libs.findLibrary("dagger-hilt-compiler").get())

            // ---- Local unit tests (optional / Robolectric) ----
            add("testImplementation", libs.findLibrary("dagger-hilt-testing").get())
            add("kaptTest", libs.findLibrary("dagger-hilt-compiler").get())
        }

        // Set Hilt test runner for Android modules
        extensions.findByType(AppExtension::class.java)?.apply {
            defaultConfig {
                testInstrumentationRunner = "dagger.hilt.android.testing.HiltTestRunner"
            }
        }
        extensions.findByType(LibraryExtension::class.java)?.apply {
            defaultConfig {
                testInstrumentationRunner = "dagger.hilt.android.testing.HiltTestRunner"
            }
        }
    }
}
