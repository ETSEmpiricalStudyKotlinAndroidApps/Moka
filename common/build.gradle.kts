import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("native.cocoapods")
    id("kotlinx-serialization")
    id("com.apollographql.apollo3").version(Versions.apollo)
    id("com.google.devtools.ksp").version(Versions.kspApi)
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

                // Kotlin
                api(Deps.Kotlin.stdlib)
                api(Deps.Kotlin.coroutinesCore)
                api(Deps.Kotlin.coroutinesAndroid)

                // compose common
                api(Deps.AndroidX.UI.tooling)
                api(Deps.AndroidX.UI.foundation)

                api(Deps.AndroidX.activityCompose)
                api(Deps.AndroidX.Paging.runtime)
                api(Deps.AndroidX.Paging.compose)
                api(Deps.AndroidX.Lifecycle.liveDataKtx)
                api(Deps.AndroidX.Lifecycle.lifecycleExtensions)
                api(Deps.AndroidX.Lifecycle.viewModelCompose)
                api(Deps.AndroidX.Lifecycle.runtimeKtx)
                api(Deps.AndroidX.Navigation.common)
                api(Deps.AndroidX.Navigation.compose)

                api(Deps.AndroidX.dataStore)

                implementation(Deps.Kotlin.serializationProtoBuf)

                api(Deps.AndroidX.Room.common)
                api(Deps.AndroidX.Room.runtime)
                api(Deps.AndroidX.Room.migration)
                api(Deps.AndroidX.Room.ktx)
                api(Deps.AndroidX.Room.paging)

                api(Deps.logcat)
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

dependencies {
    ksp(Deps.AndroidX.Room.compiler)
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
                "Date" to "kotlinx.datetime.LocalDate",
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-Xskip-prerelease-check",
            "-Xjvm-default=compatibility"
        )
    }
}