object Versions {

    val compileSdk = 29
    val targetSdk = 29
    val minSdk = 23

    val androidGradle = "3.4.1"
    val gmsGoogleServiceGradle = "4.2.0"
    val fabricToolsGradle = "1.29.0"
    val kotlin = "1.3.40"
    val coroutines = "1.2.2"
    val fragment = "1.1.0-beta01"
    val constraintLayout = "2.0.0-beta2"
    val coordinatorLayout = "1.1.0-beta01"
    val appcompat = "1.1.0-beta01"
    val databinding = "3.2.0-alpha11"
    val drawerLayout = "1.1.0-alpha02"
    val material = "1.1.0-alpha07"
    val viewpager2 = "1.0.0-alpha05"
    val browser = "1.0.0"
    val lifecycle = "2.2.0-alpha01"
    val navigation = "2.1.0-alpha05"
    val navigationTesting = "1.0.0-alpha08"
    val paging = "2.1.0"
    val workManager = "2.1.0-beta02"
    val room = "2.1.0"
    val roomCoroutines = "2.1.0-alpha04"
    val matisse = "0.5.2-beta4"
    val preference = "1.1.0-beta01"
    val swipeRefreshLayout = "1.1.0-alpha01"
    val glide = "4.9.0"
    val lottie = "3.0.7"
    val retrofit = "2.6.0"
    val okhttpLoggingInterceptor = "3.14.2"
    val apollo = "1.0.1-SNAPSHOT"
    val commonMark = "0.12.1"
    val jsoup = "1.11.3"
    val timber = "4.7.1"
    val firebaseCore = "17.0.0"
    val crashlytics = "2.10.1"
    val junit = "4.12"
    val androidJunit = "1.1.0"
    val androidTestCore = "1.1.0"
    val androidTestRunner = "1.1.1"
    val androidTestRules = "1.1.1"
    val espresso = "3.1.2-alpha01"
    val mockito = "2.8.47"
    val hamcrest = "1.3"

}

object Deps {

    val matisse = "com.zhihu.android:matisse:${Versions.matisse}"
    val jsoup = "org.jsoup:jsoup:${Versions.jsoup}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    object GradlePlugin {

        val android = "com.android.tools.build:gradle:${Versions.androidGradle}"
        val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        val apollo = "com.apollographql.apollo:apollo-gradle-plugin:${Versions.apollo}"
        val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
        val googleServices = "com.google.gms:google-services:${Versions.gmsGoogleServiceGradle}"
        val fabric = "io.fabric.tools:gradle:${Versions.fabricToolsGradle}"

    }

    object Kotlin {

        val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
        val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
        val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"

    }

    object AndroidX {

        val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:${Versions.coordinatorLayout}"
        val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
        val viewpager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
        val browser = "androidx.browser:browser:${Versions.browser}"
        val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
        val preference = "androidx.preference:preference:${Versions.preference}"
        val drawerLayout = "androidx.drawerlayout:drawerlayout:${Versions.drawerLayout}"

        object Lifecycle {

            val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"

        }

        object Navigation {

            val navigationFragmentKtx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
            val navigationUIKtx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"

        }

        object Paging {

            val common = "androidx.paging:paging-common:${Versions.paging}"
            val runtime = "androidx.paging:paging-runtime-ktx:${Versions.paging}"

        }

        object Room {

            val common = "androidx.room:room-common:${Versions.room}"
            val compiler = "androidx.room:room-compiler:${Versions.room}"
            val runtime = "androidx.room:room-runtime:${Versions.room}"
            val coroutines = "androidx.room:room-coroutines:${Versions.roomCoroutines}"
            val migration = "androidx.room:room-migration:${Versions.room}"

        }

        object WorkManager {

            val runtime = "androidx.work:work-runtime-ktx:${Versions.workManager}"

        }

        object DataBinding {

            val compiler = "androidx.databinding:compiler:${Versions.databinding}"

        }

    }

    object Google {

        val material = "com.google.android.material:material:${Versions.material}"
        val crashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.crashlytics}"
        val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebaseCore}"

    }

    object Glide {

        val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
        val okhttp3Integration = "com.github.bumptech.glide:okhttp3-integration:${Versions.glide}"
        val compiler = "com.github.bumptech.glide:compiler:${Versions.glide}"

    }

    object Retrofit {

        val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        val gsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"

    }

    object OkHttp {

        val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttpLoggingInterceptor}"

    }

    object Apollo {

        val runtime = "com.apollographql.apollo:apollo-runtime:${Versions.apollo}"
        val androidSupport = "com.apollographql.apollo:apollo-android-support:${Versions.apollo}"
        val coroutinesSupport = "com.apollographql.apollo:apollo-coroutines-support:${Versions.apollo}"

    }

    object Airbnb {

        val lottie = "com.airbnb.android:lottie:${Versions.lottie}"

    }

    object CommonMark {

        val commonMark = "com.atlassian.commonmark:commonmark:${Versions.commonMark}"
        val autolink = "com.atlassian.commonmark:commonmark-ext-autolink:${Versions.commonMark}"
        val gfmStrikethrough = "com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:${Versions.commonMark}"
        val gfmTables = "com.atlassian.commonmark:commonmark-ext-gfm-tables:${Versions.commonMark}"
        val ins = "com.atlassian.commonmark:commonmark-ext-ins:${Versions.commonMark}"
        val headingAnchor = "com.atlassian.commonmark:commonmark-ext-heading-anchor:${Versions.commonMark}"
        val yamlFrontMatter = "com.atlassian.commonmark:commonmark-ext-yaml-front-matter:${Versions.commonMark}"

    }

    object Test {

        val junit = "junit:junit:${Versions.junit}"
        val pagingCommon = "androidx.paging:paging-common:${Versions.paging}"
        val mockitoCore = "org.mockito:mockito-core:${Versions.mockito}"
        val hamcrestAll = "org.hamcrest:hamcrest-all:${Versions.hamcrest}"
        val retrofitMock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"

    }

    object AndroidTest {

        val testCore = "androidx.test:core:${Versions.androidTestCore}"
        val testRunner = "androidx.test:runner:${Versions.androidTestRunner}"
        val testRules = "androidx.test:rules:${Versions.androidTestRules}"
        val testExtJunit = "androidx.test.ext:junit:${Versions.androidJunit}"
        val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
        val espressoIntents = "androidx.test.espresso:espresso-intents:${Versions.espresso}"
        val mockito = "org.mockito:mockito-android:${Versions.mockito}"
        val work = "androidx.work:work-testing:${Versions.workManager}"
        val navigation = "android.arch.navigation:navigation-testing:${Versions.navigationTesting}"
        val room = "androidx.room:room-testing:${Versions.room}"
        val retrofitMock = "com.squareup.retrofit2:retrofit-mock:${Versions.retrofit}"

    }

}