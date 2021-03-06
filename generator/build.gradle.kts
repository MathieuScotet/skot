group = Versions.group
version = Versions.version

plugins {
    id("java-library")
    id("kotlin")
    id("maven-publish")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}")
    implementation(project(":contract"))
    api("com.squareup:kotlinpoet:${Versions.kotlinpoet}")
    api(kotlin("reflect"))
}