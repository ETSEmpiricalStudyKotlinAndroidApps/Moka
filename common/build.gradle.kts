import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.apollographql.apollo3").version(Versions.apollo)
}

group = "io.tonnyl.moka"
version = "1.0"

repositories {
    google()
}

kotlin {
    android()

    ios {
        binaries {
            framework {
                baseName = "common"
            }
        }
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "15.0"
        podfile = project.file("../ios/Podfile")
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api(Deps.Ktor.core)
                api(Deps.Ktor.serialization)
                api(Deps.Ktor.logging)
                api(Deps.Ktor.auth)
                api(Deps.Ktor.logback)

                api(Deps.Kotlin.dateTime)
                api(Deps.Kotlin.serializationJson)

                api(Deps.Apollo.runtime)
                api(Deps.Apollo.adapters)
            }
        }
        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        named("androidMain") {
            dependencies {
                implementation(Deps.Ktor.cio)
            }
        }
        named("androidTest") {
            dependencies {
                implementation(Deps.Test.junit)
            }
        }
        named("iosMain") {
            dependencies {
                implementation(Deps.Ktor.ios)
            }
        }
        named("iosTest") { }
        named("desktopMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)

                implementation(Deps.Ktor.cio)
            }
        }
        named("desktopTest") { }
    }
}

android {
    compileSdk = Versions.compileSdk
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
    }
}

apollo {
    service("github") {
        // download the schema file from https://docs.github.com/public/schema.docs.graphql
        // and then change the extension name of the file from `graphql` to `sdl`.
        schemaFile.set(File("src/commonMain/graphql/schema.sdl"))
        packageName.set("io.tonnyl.moka.graphql")
        failOnWarnings.set(false)
        useSemanticNaming.set(true)
        generateKotlinModels.set(true)
        generateAsInternal.set(false)
        generateApolloMetadata.set(true)
        srcDir(file("src/commonMain/graphql/"))
        customScalarsMapping.set(
            mutableMapOf(
                "GitTimestamp" to "kotlinx.datetime.Instant",
                "DateTime" to "kotlinx.datetime.Instant",
                "PreciseDateTime" to "kotlinx.datetime.Instant",
                "Date" to "kotlinx.datetime.Instant",
                "HTML" to "kotlin.String",
                "URI" to "kotlin.String",
                "GitObjectID" to "kotlin.String",
                "GitSSHRemote" to "kotlin.String",
                "X509Certificate" to "kotlin.String",
                "GitRefname" to "kotlin.String"
            )
        )
    }
}