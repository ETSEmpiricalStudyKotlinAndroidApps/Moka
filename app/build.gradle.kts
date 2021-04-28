import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id("com.android.application")
    id("com.apollographql.apollo3")
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("kotlinx-serialization")
    id("com.google.protobuf").version(Versions.protoGradle)
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp").version(Versions.kspApi)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

android {
    val roomSchemaLocation = "$projectDir/schemas"

    compileSdkVersion(Versions.compileSdk)
    defaultConfig {
        applicationId = "io.github.tonnyl.moka"
        minSdkVersion(Versions.minSdk)
        targetSdkVersion(Versions.targetSdk)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        var hasPropertiesFile = false
        if (project.rootProject.file("local.properties").exists()) {
            hasPropertiesFile = true
            properties.load(project.rootProject.file("local.properties").inputStream())
        }

        if (hasPropertiesFile) {
            buildConfigField("String", "CLIENT_ID", "\"${properties["CLIENT_ID"]}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${properties["CLIENT_SECRET"]}\"")
        } else { // CI
            buildConfigField("String", "CLIENT_ID", "\"client_id_placeholder\"")
            buildConfigField("String", "CLIENT_SECRET", "\"client_secret_placeholder\"")
        }

    }

    sourceSets {
        getByName("androidTest") {
            assets.srcDirs(roomSchemaLocation)
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            versionNameSuffix = "-debug"
            isTestCoverageEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        useIR = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.ui
    }

    // https://github.com/Kotlin/kotlinx.coroutines/issues/2023
    configurations.all {
        resolutionStrategy {
            exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
        }
    }
}

apollo {
    service("github") {
        // download the schema file from https://docs.github.com/public/schema.docs.graphql
        // and then change the extension name of the file from `graphql` to `sdl`.
        schemaFile.set(File("src/main/graphql/schema.sdl"))
        rootPackageName.set("io.github.tonnyl.moka")
        failOnWarnings.set(false)
        useSemanticNaming.set(true)
        addGraphqlDirectory(file("src/main/graphql/"))
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

protobuf {
    protoc {
        artifact = Deps.Google.protoc
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins.create("java")
        }
    }
}

dependencies {
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))

    // Kotlin
    implementation(Deps.Kotlin.stdlib)
    implementation(Deps.Kotlin.coroutinesCore)
    implementation(Deps.Kotlin.coroutinesAndroid)
    implementation(Deps.Kotlin.dateTime)
    implementation(Deps.Kotlin.serialization)

    // AndroidX
    implementation(Deps.AndroidX.browser)
    implementation(Deps.AndroidX.dataStore)
    implementation(Deps.AndroidX.activityCompose)
    implementation(Deps.AndroidX.Lifecycle.lifecycleExtensions)
    implementation(Deps.AndroidX.Lifecycle.liveDataKtx)
    implementation(Deps.AndroidX.Lifecycle.viewModelCompose)
    implementation(Deps.AndroidX.Lifecycle.runtimeKtx)
    implementation(Deps.AndroidX.Navigation.compose)
    implementation(Deps.AndroidX.WorkManager.runtime)
    implementation(Deps.AndroidX.Paging.runtime)
    implementation(Deps.AndroidX.Paging.compose)
    implementation(Deps.AndroidX.Room.common)
    implementation(Deps.AndroidX.Room.runtime)
    implementation(Deps.AndroidX.Room.migration)
    implementation(Deps.AndroidX.Room.ktx)
    ksp(Deps.AndroidX.Room.compiler)

    implementation(Deps.AndroidX.UI.runtime)
    implementation(Deps.AndroidX.UI.animation)
    implementation(Deps.AndroidX.UI.core)
    implementation(Deps.AndroidX.UI.foundation)
    implementation(Deps.AndroidX.UI.material)
    implementation(Deps.AndroidX.UI.layout)
    implementation(Deps.AndroidX.UI.liveData)
    implementation(Deps.AndroidX.UI.tooling)
    implementation(Deps.AndroidX.UI.constraintLayout)

    // Google
    implementation(Deps.Google.firebaseAnalyticsKtx)
    implementation(Deps.Google.firebaseCrashlytics)
    implementation(Deps.Google.protobufJava)
    implementation(Deps.Google.material)
    implementation(Deps.Google.Accompanist.coil)
    implementation(Deps.Google.Accompanist.insets)
    implementation(Deps.Google.Accompanist.swipeRefresh)

    // OkHttp
    implementation(Deps.OkHttp.loggingInterceptor)

    // Apollo
    implementation(Deps.Apollo.runtime)

    // Airbnb
    implementation(Deps.Airbnb.lottie)

    // CommonMark
    implementation(Deps.CommonMark.commonMark)
    implementation(Deps.CommonMark.autolink)
    implementation(Deps.CommonMark.gfmStrikethrough)
    implementation(Deps.CommonMark.gfmTables)
    implementation(Deps.CommonMark.ins)
    implementation(Deps.CommonMark.headingAnchor)
    implementation(Deps.CommonMark.yamlFrontMatter)

    implementation(Deps.jsoup)
    implementation(Deps.timber)

    implementation(Deps.Ktor.core)
    implementation(Deps.Ktor.logging)
    implementation(Deps.Ktor.okhttpClient)
    implementation(Deps.Ktor.serialization)

    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.mockitoCore)
    testImplementation(Deps.Test.hamcrestAll)

    androidTestImplementation(Deps.AndroidTest.testCore)
    androidTestImplementation(Deps.AndroidTest.testRunner)
    androidTestImplementation(Deps.AndroidTest.testRules)
    androidTestImplementation(Deps.AndroidTest.testExtJunit)
    androidTestImplementation(Deps.AndroidTest.espressoCore)
    androidTestImplementation(Deps.AndroidTest.espressoContrib)
    androidTestImplementation(Deps.AndroidTest.espressoIntents)
    androidTestImplementation(Deps.AndroidTest.espressoAccessibility)
    androidTestImplementation(Deps.AndroidTest.mockito)
    androidTestImplementation(Deps.AndroidTest.work)
    androidTestImplementation(Deps.AndroidTest.room)
    androidTestImplementation(Deps.AndroidTest.uiTest)
    androidTestImplementation(Deps.AndroidTest.uiTestJunit4)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-XXLanguage:+InlineClasses",
            "-Xskip-prerelease-check"
        )
    }
}

apply(plugin = "com.google.gms.google-services")
