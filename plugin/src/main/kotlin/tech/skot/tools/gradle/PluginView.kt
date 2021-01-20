package tech.skot.tools.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.model.KotlinAndroidExtension
import tech.skot.Versions

//open class SKPluginViewExtension {
//}

class PluginView: Plugin<Project> {

    override fun apply(project: Project) {
//        val extension = project.extensions.create<SKPluginViewExtension>("skot")
        project.plugins.apply("com.android.library")
        project.plugins.apply("com.github.ben-manes.versions")

        project.extensions.findByType(LibraryExtension::class)?.android()

        project.dependencies {
            dependencies()
        }

    }


    private fun LibraryExtension.android() {

        sourceSets.getByName("main") {
            java.srcDir("src/main/kotlin")
            java.srcDir("generated/main/kotlin")
        }

        defaultConfig {
            minSdkVersion(Versions.android_minSdk)
        }
        compileSdkVersion(Versions.android_compileSdk)

        lintOptions {
            isAbortOnError = false
        }

        buildFeatures {
            viewBinding = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

    }




    private fun DependencyHandlerScope.dependencies() {
        add("api", "tech.skot:view:${Versions.skot}")
        add("api", project(":contract"))
    }

}