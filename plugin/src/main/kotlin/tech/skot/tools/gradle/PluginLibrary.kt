package tech.skot.tools.gradle

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import tech.skot.Versions
import kotlin.reflect.KClass

//open class SKPluginLibraryExtension {
//    var message: String? = null
//}

class PluginLibrary: Plugin<Project> {

    override fun apply(project: Project) {
//        val extension = project.extensions.create<SKPluginLibraryExtension>("skot")
        project.plugins.apply("com.android.library")
        project.plugins.apply("maven-publish")
        project.plugins.apply("kotlinx-serialization")

        project.extensions.findByType(LibraryExtension::class)?.conf()

        project.extensions.findByType(KotlinMultiplatformExtension::class)?.conf()

    }


    private fun LibraryExtension.conf() {

        defaultConfig {
            minSdkVersion(Versions.android_minSdk)
        }
        compileSdkVersion(Versions.android_compileSdk)

        sourceSets {
            getByName("main").java.srcDirs("src/androidMain/kotlin")
            getByName("main").manifest.srcFile("src/androidMain/AndroidManifest.xml")
            getByName("main").res.srcDir("src/androidMain/res")
            getByName("test").java.srcDirs("src/javaTest/kotlin")
        }
    }

    private fun KotlinMultiplatformExtension.conf() {

        jvm("jvm")

        android("android") {
            publishLibraryVariants("release", "debug")
            publishLibraryVariantsGroupedByFlavor = true
        }

        sourceSets["commonMain"].kotlin.srcDir("src/contract/kotlin")
        sourceSets["commonMain"].dependencies {
            api("tech.skot:viewcontract:${Versions.skot}")
        }

        sourceSets["androidMain"].kotlin.srcDir("src/viewmodel/kotlin")

        sourceSets["androidMain"].dependencies {
            implementation("tech.skot:viewmodel:${Versions.skot}")
        }

//        sourceSets["androidMain"].dependencies {
//        }



    }


}