package tech.skot.tools.starter.androidApp

import com.squareup.kotlinpoet.*
import tech.skot.tools.starter.BuildGradleGenerator
import tech.skot.tools.starter.ModuleGenerator
import tech.skot.tools.starter.StarterGenerator


const val startGradleAndroidBlock = """android {

    defaultConfig {
        applicationId = "###APPLICATION_ID###"
        
        versionCode = Build.versionCode
        versionName = Build.versionName
    }

//    signingConfigs {
//        create("release") {
//            val signingProps = Build.readSigningProperties("${'$'}projectDir")
//            keyAlias = signingProps.getProperty("key.alias")
//            keyPassword = signingProps.getProperty("key.password")
//            storeFile = file("signing/keystore.jks")
//            storePassword = signingProps.getProperty("store.password")
//        }
//    }


    buildTypes {
        getByName("debug") {
            applicationIdSuffix = "dev"
            manifestPlaceholders["app_name"] = "Dev ${'$'}{Build.appName}"
        }

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

//            signingConfig = signingConfigs.getByName("release")

            manifestPlaceholders["app_name"] = Build.appName
        }
    }

}"""

fun StarterGenerator.androidApp(){
    ModuleGenerator("androidApp", configuration, rootDir).apply {

        androidApplicationClass = "${configuration.appName}Application"

        val androidApplicationId = configuration.appPackage+".android"

        androidPackage = androidApplicationId
        mainPackage = configuration.appPackage
        justAndroid = true

        buildGradle {
            plugin(BuildGradleGenerator.Plugin.Id("skot-app"))
            plugin(BuildGradleGenerator.Plugin.Kotlin("android"))

            androidBlock = startGradleAndroidBlock.replace("###APPLICATION_ID###", androidApplicationId)

        }



        FileSpec.builder(configuration.appPackage+".android", androidApplicationClass!!)
                .addType(
                        TypeSpec.classBuilder(androidApplicationClass!!)
                                .superclass(ClassName("android.app", "Application"))
                                .addFunction(FunSpec.builder("onCreate")
                                        .addModifiers(KModifier.OVERRIDE)
                                        .addCode(
"""super.onCreate()
Timber.plant(Timber.DebugTree())
injector = BaseInjector(this,
    listOf(
            appModule,
            viewModelModule,
            viewmodelAndroidModule,
            modelAndroidModule,
            modelFrameworkModule,
            viewFrameworkModule,
            viewModule,
            modelModule
    ))
"""
                                        )
                                        .build())
                                .build()
                )
                .addImport(ClassName("timber.log","Timber"))
                .build()
                .writeTo(rootDir.resolve("androidApp/src/main/kotlin"))


    }.generate()
    modules.add("androidApp")
}