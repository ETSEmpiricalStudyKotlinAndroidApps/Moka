object Versions {

    const val compileSdk = 31
    const val targetSdk = 31
    const val minSdk = 23

    const val androidGradle = "7.0.4"
    const val gmsGoogleServiceGradle = "4.3.10"
    const val firebaseCrashlyticsGradle = "2.8.1"
    const val desugaring = "1.1.5"
    const val kotlin = "1.6.0"
    const val dateTime = "0.3.0"
    const val coroutines = "1.6.0-RC"
    const val serializationJson = "1.3.1"
    const val serializationProtoBuf = "1.3.1"
    const val browser = "1.4.0-beta01"
    const val lifecycle = "2.2.0"
    const val liveDataKtx = "2.4.0-rc01"
    const val lifecycleRuntimeKtx = "2.4.0-rc01"
    const val viewModelCompose = "2.4.0"
    const val navigation = "2.4.0-beta02"
    const val paging = "3.1.0-beta01"
    const val pagingCompose = "1.0.0-alpha14"
    const val workManager = "2.7.1"
    const val room = "2.4.0-rc01"
    const val ui = "1.1.0-beta04"
    const val composeDesktop = "1.0.0"
    const val constraintLayoutCompose = "1.0.0-rc02"
    const val dataStore = "1.0.0"
    const val activityCompose = "1.4.0"
    const val splashScreen = "1.0.0-alpha02"
    const val accompanist = "0.21.4-beta"
    const val material = "1.6.0-alpha01"
    const val material3 = "1.0.0-alpha02"
    const val kspApi = "1.6.0-1.0.1"
    const val lottie = "4.2.2"
    const val okhttpLoggingInterceptor = "4.7.2"
    const val apollo = "3.0.0-rc02"
    const val commonMark = "0.15.2"
    const val coil = "1.4.0"
    const val jsoup = "1.12.1"
    const val logcat = "0.1"
    const val ktor = "1.6.6"
    const val logback = "1.3.0-alpha10"
    const val firebaseAnalyticsKtx = "20.0.0"
    const val firebaseCrashlytics = "18.0.0"
    const val junit = "4.13"
    const val androidJunit = "1.1.3"
    const val androidTestCore = "1.4.0"
    const val androidTestRunner = "1.4.0"
    const val androidTestRules = "1.4.0"
    const val espresso = "3.4.0"
    const val mockito = "3.5.13"
    const val hamcrest = "1.3"

}

object Deps {

    const val jsoup = "org.jsoup:jsoup:${Versions.jsoup}"
    const val logcat = "com.squareup.logcat:logcat:${Versions.logcat}"
    const val desugaring = "com.android.tools:desugar_jdk_libs:${Versions.desugaring}"
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coil}"

    object GradlePlugin {

        const val android = "com.android.tools.build:gradle:${Versions.androidGradle}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val googleServices =
            "com.google.gms:google-services:${Versions.gmsGoogleServiceGradle}"
        const val firebaseCrashlyticsGradle =
            "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlyticsGradle}"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        const val compose = "org.jetbrains.compose:compose-gradle-plugin:${Versions.composeDesktop}"

    }

    object Kotlin {

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.dateTime}"
        const val serializationJson =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serializationJson}"
        const val serializationProtoBuf =
            "org.jetbrains.kotlinx:kotlinx-serialization-protobuf:${Versions.serializationProtoBuf}"

    }

    object AndroidX {

        const val browser = "androidx.browser:browser:${Versions.browser}"
        const val dataStore = "androidx.datastore:datastore:${Versions.dataStore}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
        const val splashScreen = "androidx.core:core-splashscreen:${Versions.splashScreen}"

        object Lifecycle {

            const val lifecycleExtensions =
                "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
            const val liveDataKtx =
                "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveDataKtx}"
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.viewModelCompose}"
            const val runtimeKtx =
                "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"

        }

        object Navigation {

            const val common = "androidx.navigation:navigation-common:${Versions.navigation}"
            const val compose =
                "androidx.navigation:navigation-compose:${Versions.navigation}"

        }

        object Paging {

            const val runtime = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
            const val compose = "androidx.paging:paging-compose:${Versions.pagingCompose}"

        }

        /**
         * All room libraries should have the same version.
         */
        object Room {

            const val common = "androidx.room:room-common:${Versions.room}"
            const val compiler = "androidx.room:room-compiler:${Versions.room}"
            const val runtime = "androidx.room:room-runtime:${Versions.room}"
            const val migration = "androidx.room:room-migration:${Versions.room}"
            const val ktx = "androidx.room:room-ktx:${Versions.room}"
            const val paging = "androidx.room:room-paging:${Versions.room}"

        }

        object WorkManager {

            const val runtime = "androidx.work:work-runtime-ktx:${Versions.workManager}"

        }

        object UI {

            const val runtime = "androidx.compose.runtime:runtime:${Versions.ui}"
            const val core = "androidx.compose.ui:ui:${Versions.ui}"
            const val layout = "androidx.compose.foundation:foundation-layout:${Versions.ui}"
            const val material = "androidx.compose.material:material:${Versions.ui}"
            const val foundation = "androidx.compose.foundation:foundation:${Versions.ui}"
            const val animation = "androidx.compose.animation:animation:${Versions.ui}"
            const val tooling = "androidx.compose.ui:ui-tooling:${Versions.ui}"
            const val liveData = "androidx.compose.runtime:runtime-livedata:${Versions.ui}"
            const val constraintLayout =
                "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintLayoutCompose}"

        }

    }

    object Google {

        object Accompanist {

            const val insets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"
            const val swipeRefresh =
                "com.google.accompanist:accompanist-swiperefresh:${Versions.accompanist}"
            const val pager = "com.google.accompanist:accompanist-pager:${Versions.accompanist}"
            const val pagerIndicators =
                "com.google.accompanist:accompanist-pager-indicators:${Versions.accompanist}"
            const val flowLayout =
                "com.google.accompanist:accompanist-flowlayout:${Versions.accompanist}"
            const val placeholder =
                "com.google.accompanist:accompanist-placeholder-material:${Versions.accompanist}"
            const val systemUiController =
                "com.google.accompanist:accompanist-systemuicontroller:${Versions.accompanist}"

        }

        const val firebaseAnalyticsKtx =
            "com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalyticsKtx}"
        const val firebaseCrashlytics =
            "com.google.firebase:firebase-crashlytics:${Versions.firebaseCrashlytics}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val material3 = "androidx.compose.material3:material3:${Versions.material3}"
        const val kspApi = "com.google.devtools.ksp:symbol-processing-api:${Versions.kspApi}"

    }

    object Ktor {

        const val core = "io.ktor:ktor-client-core:${Versions.ktor}"
        const val logging = "io.ktor:ktor-client-logging:${Versions.ktor}"
        const val serialization = "io.ktor:ktor-client-serialization:${Versions.ktor}"
        const val logback = "ch.qos.logback:logback-classic:${Versions.logback}"
        const val auth = "io.ktor:ktor-client-auth:${Versions.ktor}"
        const val cio = "io.ktor:ktor-client-cio:${Versions.ktor}"
        const val ios = "io.ktor:ktor-client-ios:${Versions.ktor}"

    }

    object OkHttp {

        const val loggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLoggingInterceptor}"

    }

    object Apollo {

        const val runtime = "com.apollographql.apollo3:apollo-runtime:${Versions.apollo}"
        const val adapters = "com.apollographql.apollo3:apollo-adapters:${Versions.apollo}"

    }

    object Airbnb {

        const val lottie = "com.airbnb.android:lottie-compose:${Versions.lottie}"

    }

    object CommonMark {

        const val commonMark = "com.atlassian.commonmark:commonmark:${Versions.commonMark}"
        const val autolink =
            "com.atlassian.commonmark:commonmark-ext-autolink:${Versions.commonMark}"
        const val gfmStrikethrough =
            "com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:${Versions.commonMark}"
        const val gfmTables =
            "com.atlassian.commonmark:commonmark-ext-gfm-tables:${Versions.commonMark}"
        const val ins = "com.atlassian.commonmark:commonmark-ext-ins:${Versions.commonMark}"
        const val headingAnchor =
            "com.atlassian.commonmark:commonmark-ext-heading-anchor:${Versions.commonMark}"
        const val yamlFrontMatter =
            "com.atlassian.commonmark:commonmark-ext-yaml-front-matter:${Versions.commonMark}"

    }

    object Test {

        const val junit = "junit:junit:${Versions.junit}"
        const val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
        const val hamcrestAll = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"

    }

    object AndroidTest {

        const val testCore = "androidx.test:core-ktx:${Versions.androidTestCore}"
        const val testRunner = "androidx.test:runner:${Versions.androidTestRunner}"
        const val testRules = "androidx.test:rules:${Versions.androidTestRules}"
        const val testExtJunit = "androidx.test.ext:junit-ktx:${Versions.androidJunit}"
        const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
        const val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
        const val espressoAccessibility =
            "androidx.test.espresso:espresso-accessibility:${Versions.espresso}"
        const val mockito = "org.mockito:mockito-android:${Versions.mockito}"
        const val work = "androidx.work:work-testing:${Versions.workManager}"
        const val room = "androidx.room:room-testing:${Versions.room}"
        const val uiTest = "androidx.compose.ui:ui-test:${Versions.ui}"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${Versions.ui}"

    }

}