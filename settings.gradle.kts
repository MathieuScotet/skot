rootProject.name = "SKot Framework"
rootProject.buildFileName = "build.gradle.kts"
include(":core")
include(":contract")
include(":view")
include(":viewmodel")
include(":model")
include("generator")
include(":androidTests")
include(":plugin")
enableFeaturePreview("GRADLE_METADATA")