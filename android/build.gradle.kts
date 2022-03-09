import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    kotlin("android")
    id("kotlin-parcelize")
    id("kotlinx-serialization")
    id("com.google.firebase.crashlytics")
}

android {
    val roomSchemaLocation = "$projectDir/schemas"

    compileSdk = Versions.compileSdk

    val localProperties = gradleLocalProperties(rootDir)
    val hasPropertiesFile = localProperties.getProperty("STORE_FILE_PATH") != null

    val versionProperties = Properties()
    val versionPropertiesFile = project.rootProject.file("version.properties")
    versionProperties.load(versionPropertiesFile.inputStream())

    defaultConfig {
        applicationId = "io.github.tonnyl.moka"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = versionProperties.getProperty("versionCode").toInt()
        versionName = versionProperties.getProperty("versionName")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val releaseSignConfig = "release"
    signingConfigs {
        if (hasPropertiesFile) {
            create(releaseSignConfig) {
                storeFile = rootProject.file(localProperties.getProperty("STORE_FILE_PATH"))
                storePassword = localProperties.getProperty("STORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS")
                keyPassword = localProperties.getProperty("KEY_PASSWORD")
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
            if (hasPropertiesFile) {
                signingConfig = signingConfigs.getByName(releaseSignConfig)
            }
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
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
        viewBinding = true
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

dependencies {
    implementation(fileTree(Pair("dir", "libs"), Pair("include", listOf("*.jar"))))

    implementation(project(":common"))

    coreLibraryDesugaring(Deps.desugaring)

    // AndroidX
    implementation(Deps.AndroidX.browser)
    implementation(Deps.AndroidX.splashScreen)
    implementation(Deps.AndroidX.WorkManager.runtime)
    implementation(Deps.AndroidX.glance)
    implementation(Deps.AndroidX.swipeRefresh)

    implementation(Deps.AndroidX.UI.runtime)
    implementation(Deps.AndroidX.UI.animation)
    implementation(Deps.AndroidX.UI.core)
    implementation(Deps.AndroidX.UI.foundation)
    implementation(Deps.AndroidX.UI.material)
    implementation(Deps.AndroidX.UI.layout)
    implementation(Deps.AndroidX.UI.liveData)
    implementation(Deps.AndroidX.UI.viewBinding)
    implementation(Deps.AndroidX.UI.constraintLayout)

    implementation(Deps.Google.Firebase.analyticsKtx)
    implementation(Deps.Google.Firebase.crashlytics)
    implementation(platform(Deps.Google.Firebase.bom))

    implementation(Deps.Google.material)
    implementation(Deps.Google.material3)
    implementation(Deps.Google.Accompanist.insets)
    implementation(Deps.Google.Accompanist.swipeRefresh)
    implementation(Deps.Google.Accompanist.pager)
    implementation(Deps.Google.Accompanist.pagerIndicators)
    implementation(Deps.Google.Accompanist.flowLayout)
    implementation(Deps.Google.Accompanist.placeholder)
    implementation(Deps.Google.Accompanist.systemUiController)
    implementation(Deps.Google.ExoPlayer.core)
    implementation(Deps.Google.ExoPlayer.ui)

    // OkHttp
    implementation(Deps.OkHttp.loggingInterceptor)

    // Airbnb
    implementation(Deps.Airbnb.lottie)

    implementation(Deps.Coil.compose)
    implementation(Deps.Coil.gif)
    implementation(Deps.Coil.svg)

    // CommonMark
    implementation(Deps.CommonMark.commonMark)
    implementation(Deps.CommonMark.autolink)
    implementation(Deps.CommonMark.gfmStrikethrough)
    implementation(Deps.CommonMark.gfmTables)
    implementation(Deps.CommonMark.ins)
    implementation(Deps.CommonMark.headingAnchor)
    implementation(Deps.CommonMark.yamlFrontMatter)

    implementation(Deps.jsoup)

    debugImplementation(Deps.leakCanary)

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
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xskip-prerelease-check",
            "-Xjvm-default=compatibility",
            "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
            "-Xopt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-Xopt-in=androidx.paging.ExperimentalPagingApi",
            "-Xopt-in=com.google.accompanist.insets.ExperimentalAnimatedInsets",
            "-Xopt-in=com.google.accompanist.pager.ExperimentalPagerApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=coil.annotation.ExperimentalCoilApi"
        )
    }
}
