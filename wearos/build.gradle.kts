import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = Versions.compileSdk

    val localProperties = Properties()
    var hasPropertiesFile = false
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        hasPropertiesFile = true
        localProperties.load(localPropertiesFile.inputStream())
    }

    val versionProperties = Properties()
    val versionPropertiesFile = project.rootProject.file("version.properties")
    versionProperties.load(versionPropertiesFile.inputStream())

    defaultConfig {
        applicationId = "io.github.tonnyl.moka.wearos"
        minSdk = Versions.wearOSMinSdk
        targetSdk = Versions.targetSdk
        versionCode = versionProperties.getProperty("versionCode").toInt()
        versionName = versionProperties.getProperty("versionName")

        if (hasPropertiesFile) {
            buildConfigField("String", "CLIENT_ID", "\"${localProperties["CLIENT_ID"]}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${localProperties["CLIENT_SECRET"]}\"")
        } else { // CI
            buildConfigField("String", "CLIENT_ID", "\"client_id_placeholder\"")
            buildConfigField("String", "CLIENT_SECRET", "\"client_secret_placeholder\"")
        }
    }

    val releaseSignConfig = "release"
    if (hasPropertiesFile) {
        signingConfigs {
            create(releaseSignConfig) {
                storeFile = rootProject.file(localProperties.getProperty("STORE_FILE_PATH"))
                storePassword = localProperties.getProperty("STORE_PASSWORD")
                keyAlias = localProperties.getProperty("KEY_ALIAS")
                keyPassword = localProperties.getProperty("KEY_PASSWORD")
            }
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

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.ui
    }

    sourceSets.forEach {
        it.java {
            srcDirs("src/${it.name}/kotlin")
        }
    }
}

dependencies {
    implementation(project(":common"))

    implementation(Deps.Google.wearOS)
    implementation(Deps.Google.wear)

    // compose wear
    implementation(Deps.AndroidX.UI.wearFoundation)
    implementation(Deps.AndroidX.UI.wearMaterial)
    implementation(Deps.AndroidX.UI.wearNavigation)
}
