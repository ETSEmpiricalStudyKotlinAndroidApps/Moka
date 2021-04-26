object Versions {

    const val compileSdk = 30
    const val targetSdk = 30
    const val minSdk = 23

    const val androidGradle = "7.0.0-alpha14"
    const val gmsGoogleServiceGradle = "4.3.5"
    const val firebaseCrashlyticsGradle = "2.5.1"
    const val kotlin = "1.4.32"
    const val dateTime = "0.1.1"
    const val coroutines = "1.4.3"
    const val serialization = "1.1.0"
    const val browser = "1.3.0"
    const val lifecycle = "2.2.0"
    const val liveDataKtx = "2.4.0-alpha01"
    const val viewModelCompose = "1.0.0-alpha03"
    const val navigationCompose = "1.0.0-alpha09"
    const val paging = "3.0.0-beta03"
    const val pagingCompose = "1.0.0-alpha08"
    const val workManager = "2.7.0-alpha02"
    const val room = "2.3.0-rc01"
    const val ui = "1.0.0-beta03"
    const val constraintLayoutCompose = "1.0.0-alpha05"
    const val dataStore = "1.0.0-alpha08"
    const val activityCompose = "1.3.0-alpha05"
    const val accompanist = "0.7.0"
    const val material = "1.4.0-alpha02"
    const val kspApi = "1.4.31-1.0.0-alpha06"
    const val lottie = "1.0.0-beta03-2-SNAPSHOT"
    const val retrofit = "2.9.0"
    const val serializationConverter = "0.8.0"
    const val okhttpLoggingInterceptor = "4.7.2"
    const val apollo = "3.0.0-dev9-SNAPSHOT"
    const val commonMark = "0.15.2"
    const val jsoup = "1.12.1"
    const val timber = "4.7.1"
    const val firebaseAnalyticsKtx = "17.5.0"
    const val firebaseCrashlytics = "17.2.1"
    const val protoc = "3.14.0"
    const val protobufJavaLite = "3.11.0"
    const val junit = "4.13"
    const val androidJunit = "1.1.3-alpha05"
    const val androidTestCore = "1.4.0-alpha05"
    const val androidTestRunner = "1.4.0-alpha05"
    const val androidTestRules = "1.4.0-alpha05"
    const val espresso = "3.4.0-alpha05"
    const val mockito = "3.5.13"
    const val hamcrest = "1.3"

}

object Deps {

    const val jsoup = "org.jsoup:jsoup:${Versions.jsoup}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    object GradlePlugin {

        const val android = "com.android.tools.build:gradle:${Versions.androidGradle}"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val googleServices =
            "com.google.gms:google-services:${Versions.gmsGoogleServiceGradle}"
        const val firebaseCrashlyticsGradle =
            "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebaseCrashlyticsGradle}"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"
        const val apollo = "com.apollographql.apollo3:apollo-gradle-plugin:${Versions.apollo}"

    }

    object Kotlin {

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
        const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.dateTime}"
        const val serialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.serialization}"

    }

    object AndroidX {

        const val browser = "androidx.browser:browser:${Versions.browser}"
        const val dataStore = "androidx.datastore:datastore:${Versions.dataStore}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"

        object Lifecycle {

            const val lifecycleExtensions =
                "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
            const val liveDataKtx =
                "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.liveDataKtx}"
            const val viewModelCompose =
                "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.viewModelCompose}"

        }

        object Navigation {

            const val compose =
                "androidx.navigation:navigation-compose:${Versions.navigationCompose}"

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
            // val coroutines = "androidx.room:room-coroutines:${Versions.room}"

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

            const val coil = "com.google.accompanist:accompanist-coil:${Versions.accompanist}"
            const val insets = "com.google.accompanist:accompanist-insets:${Versions.accompanist}"

        }

        const val firebaseAnalyticsKtx =
            "com.google.firebase:firebase-analytics-ktx:${Versions.firebaseAnalyticsKtx}"
        const val firebaseCrashlytics =
            "com.google.firebase:firebase-crashlytics:${Versions.firebaseCrashlytics}"
        const val protoc = "com.google.protobuf:protoc:${Versions.protoc}"
        const val protobufJavaLite =
            "com.google.protobuf:protobuf-javalite:${Versions.protobufJavaLite}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val kspApi = "com.google.devtools.ksp:symbol-processing-api:${Versions.kspApi}"

    }

    object Retrofit {

        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val serializationConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.serializationConverter}"

    }

    object OkHttp {

        const val loggingInterceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLoggingInterceptor}"

    }

    object Apollo {

        const val runtime = "com.apollographql.apollo3:apollo-runtime-kotlin:${Versions.apollo}"

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
        const val retrofitMock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"

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