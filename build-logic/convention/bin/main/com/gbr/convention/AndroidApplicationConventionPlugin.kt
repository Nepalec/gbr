package com.gbr.convention

import com.android.build.api.dsl.ApplicationExtension
import com.gbr.support.AppConfig
import com.gbr.support.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android") // Ensure project build.gradle declared this plugin
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AppConfig.targetSdk

                // Disable problematic lint detectors due to compatibility issues with Kotlin analysis API
                lint {
                    disable += "NullSafeMutableLiveData"
                    disable += "FrequentlyChangingValue"
                    disable += "RememberInComposition"
                    disable += "AutoboxingStateCreation"
                }
            }
        }
    }

}