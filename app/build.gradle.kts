import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id("com.android.application")
    id("com.apollographql.apollo").version(Versions.apollo)
    kotlin("android")
    id("kotlin-parcelize")
    kotlin("kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
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

        kapt {
            arguments {
                arg("room.schemaLocation", roomSchemaLocation)
            }
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
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
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
        dataBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerVersion = Versions.composeKotlinCompilerVersion
        kotlinCompilerExtensionVersion = Versions.ui
    }
}

apollo {
    service("github") {
        // download the schema file from https://docs.github.com/public/schema.docs.graphql
        // and then change the extension name of the file from `graphql` to `sdl`.
        schemaFile.set(File("src/main/graphql/schema.sdl"))
        rootPackageName.set("io.github.tonnyl.moka")
        suppressRawTypesWarning.set(true)
        useSemanticNaming.set(true)
        graphqlSourceDirectorySet.srcDir(file("src/main/graphql/"))
        graphqlSourceDirectorySet.include("**/*.graphql")
        customTypeMapping.set(
            mutableMapOf(
                "GitTimestamp" to "kotlinx.datetime.Instant",
                "DateTime" to "kotlinx.datetime.Instant",
                "HTML" to "kotlin.String",
                "URI" to "android.net.Uri",
                "ID" to "kotlin.String",
                "GitObjectID" to "kotlin.String",
                "GitSSHRemote" to "kotlin.String",
                "X509Certificate" to "kotlin.String"
            )
        )
    }
    generateKotlinModels.set(true)
}

dependencies {
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))

    // Kotlin
    implementation(Deps.Kotlin.stdlib)
    implementation(Deps.Kotlin.coroutinesCore)
    implementation(Deps.Kotlin.coroutinesAndroid)
    implementation(Deps.Kotlin.dateTime)

    // AndroidX
    implementation(Deps.AndroidX.constraintLayout)
    implementation(Deps.AndroidX.coordinatorLayout)
    implementation(Deps.AndroidX.swipeRefreshLayout)
    implementation(Deps.AndroidX.browser)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.preference)
    implementation(Deps.AndroidX.viewpager2)
    implementation(Deps.AndroidX.drawerLayout)
    implementation(Deps.AndroidX.recyclerView)
    implementation(Deps.AndroidX.recyclerViewSelection)
    implementation(Deps.AndroidX.Lifecycle.lifecycleExtensions)
    implementation(Deps.AndroidX.Lifecycle.liveDataKtx)
    implementation(Deps.AndroidX.Navigation.navigationFragmentKtx)
    implementation(Deps.AndroidX.Navigation.navigationUIKtx)
    implementation(Deps.AndroidX.Navigation.compose)
    implementation(Deps.AndroidX.WorkManager.runtime)
    implementation(Deps.AndroidX.Paging.common)
    implementation(Deps.AndroidX.Paging.runtime)
    implementation(Deps.AndroidX.Paging.compose)
    implementation(Deps.AndroidX.Room.common)
    implementation(Deps.AndroidX.Room.runtime)
    implementation(Deps.AndroidX.Room.migration)
    implementation(Deps.AndroidX.Room.ktx)
    // implementation(Deps.AndroidX.Room.coroutines)
    kapt(Deps.AndroidX.Room.compiler)
    implementation(Deps.AndroidX.UI.runtime)
    implementation(Deps.AndroidX.UI.animation)
    implementation(Deps.AndroidX.UI.core)
    implementation(Deps.AndroidX.UI.foundation)
    implementation(Deps.AndroidX.UI.material)
    implementation(Deps.AndroidX.UI.layout)
    implementation(Deps.AndroidX.UI.liveData)
    implementation(Deps.AndroidX.UI.tooling)

    // Google
    implementation(Deps.Google.material)
    implementation(Deps.Google.composeThemeAdapter)
    implementation(Deps.Google.firebaseAnalyticsKtx)
    implementation(Deps.Google.firebaseCrashlytics)

    // Retrofit
    implementation(Deps.Retrofit.retrofit)
    implementation(Deps.Retrofit.moshiConverter)

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

    implementation(Deps.Insetter.dbx)
    implementation(Deps.Insetter.ktx)

    implementation(Deps.Moshi.adapters)
    kapt(Deps.Moshi.kotlinCodegen)

    implementation(Deps.jsoup)
    implementation(Deps.timber)

    implementation(Deps.Accompanist.coil)
    implementation(Deps.Accompanist.insets)

    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.mockitoCore)
    testImplementation(Deps.Test.hamcrestAll)
    testImplementation(Deps.Test.retrofitMock)

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
    androidTestImplementation(Deps.AndroidTest.fragment)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOf(
            "-XXLanguage:+InlineClasses",
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check"
        )
    }
}

apply(plugin = "com.google.gms.google-services")
