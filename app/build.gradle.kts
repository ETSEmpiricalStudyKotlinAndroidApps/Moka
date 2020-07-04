import java.util.*

plugins {
    id("com.android.application")
    id("com.apollographql.apollo").version(Versions.apollo)
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.firebase.crashlytics")
}

android {
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
            buildConfigField("String", "TEST_TOKEN", "\"${properties["TEST_TOKEN"]}\"")
        } else { // CI
            buildConfigField("String", "CLIENT_ID", "\"${System.getenv("CLIENT_ID")}\"")
            buildConfigField("String", "CLIENT_SECRET", "\"${System.getenv("CLIENT_SECRET")}\"")
            buildConfigField("String", "TEST_TOKEN", "\"${System.getenv("TEST_TOKEN")}\"")
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf("room.schemaLocation" to "$projectDir/schemas")
                )
            }
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
        jvmTarget = "1.8"
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

androidExtensions {
    isExperimental = true
}

apollo {
    onCompilationUnit {
        schemaFile.set(File("/graphql/schema.json"))
        rootPackageName.set("io.github.tonnyl.moka")
        suppressRawTypesWarning.set(true)
        useSemanticNaming.set(true)
        graphqlSourceDirectorySet.setSrcDirs(listOf("/graphql/*"))
        customTypeMapping.set(
            mutableMapOf(
                "GitTimestamp" to "java.util.Date",
                "DateTime" to "java.util.Date",
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
    implementation(Deps.AndroidX.WorkManager.runtime)
    implementation(Deps.AndroidX.Paging.common)
    implementation(Deps.AndroidX.Paging.runtime)
    implementation(Deps.AndroidX.Room.common)
    implementation(Deps.AndroidX.Room.runtime)
    implementation(Deps.AndroidX.Room.migration)
    implementation(Deps.AndroidX.Room.coroutines)
    kapt(Deps.AndroidX.Room.compiler)
    implementation(Deps.AndroidX.UI.compiler)
    implementation(Deps.AndroidX.UI.runtime)
    implementation(Deps.AndroidX.UI.animation)
    implementation(Deps.AndroidX.UI.core)
    implementation(Deps.AndroidX.UI.foundation)
    implementation(Deps.AndroidX.UI.material)
    implementation(Deps.AndroidX.UI.materialIconExtended)
    implementation(Deps.AndroidX.UI.layout)
    implementation(Deps.AndroidX.UI.liveData)
    implementation(Deps.AndroidX.UI.tooling)

    // Google
    implementation(Deps.Google.material)
    implementation(Deps.Google.firebaseAnalyticsKtx)
    implementation(Deps.Google.firebaseCrashlytics)

    // Glide
    implementation(Deps.Glide.glide)
    implementation(Deps.Glide.okhttp3Integration)
    kapt(Deps.Glide.compiler)

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

apply(plugin = "com.google.gms.google-services")
