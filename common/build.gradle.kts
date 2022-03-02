import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.*
import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
    id("com.apollographql.apollo3").version(Versions.apollo)
    id("com.google.devtools.ksp").version(Versions.kspApi)
    id("com.codingfeline.buildkonfig")
}

repositories {
    google()
}

kotlin {
    android()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }

    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Deps.Ktor.core)
                api(Deps.Ktor.serialization)
                api(Deps.Ktor.logging)
                api(Deps.Ktor.auth)

                api(Deps.Kotlin.dateTime)
                api(Deps.Kotlin.serializationJson)

                api(Deps.Apollo.runtime)
                api(Deps.Apollo.adapters)

                api(Deps.Kotlin.coroutinesCore) {
                    version {
                        strictly(Versions.coroutinesNativeMt)
                    }
                }
            }
        }
        val commonTest by getting {
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
        named("iosX64Main") {
            dependencies {
                implementation(Deps.Ktor.ios)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(Deps.Ktor.ios)
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
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
    add("kspAndroid", Deps.AndroidX.Room.compiler)
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

// Don't move the position.
// Its config `packageName` has some conflicts with apollo's `packageName`.
buildkonfig {
    packageName = "io.tonnyl.moka.common.build"
    objectName = "BuildConfig"
    exposeObjectWithName = "CommonBuildConfig"

    val localProperties = gradleLocalProperties(rootDir)
    defaultConfigs {
        buildConfigField(STRING, "CLIENT_ID", "client_id_placeholder")
        buildConfigField(STRING, "CLIENT_SECRET", "client_secret_placeholder")
    }

    targetConfigs {
        create("android") {
            buildConfigField(STRING, "CLIENT_ID", "${localProperties.getProperty("ANDROID_CLIENT_ID")}")
            buildConfigField(STRING, "CLIENT_SECRET", "${localProperties.getProperty("ANDROID_CLIENT_SECRET")}")
        }
        create("ios") {
            buildConfigField(STRING, "CLIENT_ID", "${localProperties.getProperty("IOS_CLIENT_ID")}")
            buildConfigField(STRING, "CLIENT_SECRET", "${localProperties.getProperty("IOS_CLIENT_SECRET")}")
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-Xskip-prerelease-check",
            "-Xjvm-default=compatibility"
        )
    }
}

// run `generateBuildKonfig` for every build.
tasks.build {
    dependsOn("generateBuildKonfig")
}