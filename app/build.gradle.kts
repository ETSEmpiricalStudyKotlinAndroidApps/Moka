import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import java.util.*
import kotlin.collections.LinkedHashMap

plugins {
    id("com.android.application")
    id("com.apollographql.android")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("io.fabric")
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
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }

    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
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
        this as KotlinJvmOptions
        jvmTarget = "1.8"
    }

    dataBinding {
        isEnabled = true
    }
}

androidExtensions {
    isExperimental = true
}

apollo {
    val map = LinkedHashMap<String, String>().apply {
        put("GitTimestamp", "java.util.Date")
        put("DateTime", "java.util.Date")
        put("HTML", "java.lang.String")
        put("URI", "android.net.Uri")
        put("ID", "java.lang.String")
        put("GitObjectID", "java.lang.String")
        put("GitSSHRemote", "java.lang.String")
        put("X509Certificate", "java.lang.String")
    }
    customTypeMapping.set(map)

    // Java Beans Semantic Naming will result in those methods being pre-pended with get or is
//    setUseJavaBeansSemanticNaming(true)

    // Explicitly provide GraphQL schema file location and package name for generated models
    sourceSet {
        setSchemaFile("/src/main/graphql/io/github/tonnyl/moka/schema.json")
    }

    setOutputPackageName("io.github.tonnyl.moka")

//    setGenerateKotlinModels(true)
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

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
    implementation(Deps.AndroidX.Lifecycle.lifecycleExtensions)
    implementation(Deps.AndroidX.Navigation.navigationFragmentKtx)
    implementation(Deps.AndroidX.Navigation.navigationUIKtx)
    implementation(Deps.AndroidX.WorkManager.runtime)
    implementation(Deps.AndroidX.Paging.common)
    implementation(Deps.AndroidX.Paging.runtime)
    implementation(Deps.AndroidX.Room.common)
    implementation(Deps.AndroidX.Room.runtime)
    implementation(Deps.AndroidX.Room.migration)
    kapt(Deps.AndroidX.Room.compiler)
    kapt(Deps.AndroidX.DataBinding.compiler)

    // Google
    implementation(Deps.Google.material)
    implementation(Deps.Google.crashlytics)
    implementation(Deps.Google.firebaseCore)

    // Glide
    implementation(Deps.Glide.glide)
    implementation(Deps.Glide.okhttp3Integration)
    kapt(Deps.Glide.compiler)

    // Retrofit
    implementation(Deps.Retrofit.retrofit)
    implementation(Deps.Retrofit.gsonConverter)

    // OkHttp
    implementation(Deps.OkHttp.loggingInterceptor)

    // Apollo
    implementation(Deps.Apollo.runtime)
    implementation(Deps.Apollo.androidSupport)

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

    implementation(Deps.matisse)
    implementation(Deps.jsoup)
    implementation(Deps.timber)

    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.pagingCommon)
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
    androidTestImplementation(Deps.AndroidTest.mockito)
    androidTestImplementation(Deps.AndroidTest.work)
    androidTestImplementation(Deps.AndroidTest.navigation)
    androidTestImplementation(Deps.AndroidTest.room)
    androidTestImplementation(Deps.AndroidTest.fragment)
}

apply(plugin = "com.google.gms.google-services")
