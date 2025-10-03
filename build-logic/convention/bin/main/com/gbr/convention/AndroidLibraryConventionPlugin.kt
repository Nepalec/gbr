package com.gbr.convention

import com.android.build.gradle.LibraryExtension
import com.gbr.support.AppConfig
import com.gbr.support.configureKotlinAndroid
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.gradle.api.artifacts.VersionCatalogsExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val androidLibraryPublishMetaData = project.extensions.create(
            "androidLibraryPublishMetaData",
            AndroidLibraryPublishMetaData::class.java
        )

        with(project) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("maven-publish")
            }

            // Force Kotlin toolchain = 17 for the module
            extensions.configure<KotlinProjectExtension> {
                jvmToolchain(17)
            }

            extensions.configure<LibraryExtension> {
                // Keep your existing Android/Kotlin defaults
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = AppConfig.targetSdk

                // Ensure Java 17 for compile/runtime
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                // Ensure Kotlin jvmTarget = 17
                (this as ExtensionAware).extensions.configure<KotlinJvmOptions>("kotlinOptions") {
                    jvmTarget = "17"
                }
            }

            // Add Kotlin BOM aligned to the catalog's kotlin version (prevents stdlib 2.x leaks)
            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")
            dependencies {
                add(
                    "implementation",
                    platform("org.jetbrains.kotlin:kotlin-bom:" + libs.findVersion("kotlin").get().requiredVersion)
                )
            }

            afterEvaluate {
                configureAndroidLibraryPublish(androidLibraryPublishMetaData)
            }

            dependencies {
                /* Add common dependencies here (e.g., shared testing libs) */
            }
        }
    }
}

internal fun Project.configureAndroidLibraryPublish(metaData: AndroidLibraryPublishMetaData) {
    if (!metaData.isPublishEnabled) return

    afterEvaluate {
        val allVariants = (extensions.getByName("android") as LibraryExtension)
            .libraryVariants.map { it.name }.toSet()
        val eligiblePublication = metaData.publicationList.filter { it.variantName in allVariants }

        val publishing = extensions.getByType(PublishingExtension::class.java)

        eligiblePublication.forEach { publication ->
            publishing.publications.create(publication.variantName, MavenPublication::class.java) {
                groupId = publication.groupId
                artifactId = publication.artifactId
                version = publication.version

                from(components.getAt(publication.variantName))

                pom {
                    name.set(publication.name)
                    description.set(publication.description)
                }
            }
        }
    }
}

abstract class AndroidLibraryPublishMetaData {
    var isPublishEnabled: Boolean = true
    var publicationList: List<PublicationDetail> = emptyList()
}

data class PublicationDetail(
    val variantName: String,
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String? = null,
    val description: String? = null
)
