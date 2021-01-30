load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

_KOTLIN_COMPILER_VERSION = "1.4.21"

## JVM External

_RULES_JVM_EXTERNAL_VERSION = "4.0"

_RULES_JVM_EXTERNAL_SHA = "31701ad93dbfe544d597dbe62c9a1fdd76d81d8a9150c2bf1ecf928ecdf97169"

http_archive(
    name = "rules_jvm_external",
    sha256 = _RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-{}".format(_RULES_JVM_EXTERNAL_VERSION),
    urls = [
        "https://github.com/bazelbuild/rules_jvm_external/archive/{}.zip".format(_RULES_JVM_EXTERNAL_VERSION),
    ],
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

_KOTLIN_DATETIME_VERSION = "0.1.1"
_KOTLIN_COROUTINES_VERSION = "1.4.2"
_FRAGMENT_VERSION = "1.3.0-rc01"
_CONSTRAINT_LAYOUT_VERSION = "2.1.0-alpha2"
_COORDINATOR_LAYOUT_VERSION = "1.1.0"
_APPCOMPAT_VERSION = "1.3.0-beta01"
_DRAWER_LAYOUT_VERSION = "1.1.1"
_MATERIAL_VERSION = "1.3.0-rc01"
_VIEWPAGER2_VERSION = "1.1.0-alpha01"
_BROWSER_VERSION = "1.3.0"
_RECYCLER_VIEW_VERSION = "1.2.0-beta01"
_RECYCLER_VIEW_SELECTION_VERSION = "1.1.0-rc03"
_LIFECYCLE_EXTENSIONS_VERSION = "2.2.0"
_LIVE_DATA_KTX_VERSION = "2.3.0-rc01"
_NAVIGATION_VERSION = "2.3.1"
_NAVIGATION_COMPOSE_VERSION = "1.0.0-alpha05"
_PAGING_VERSION = "3.0.0-alpha12"
_PAGING_COMPOSE_VERSION = "1.0.0-alpha05"
_WORK_MANAGER_VERSION = "2.5.0-rc01"
_ROOM_VERSION = "2.3.0-alpha04"
_SWIPE_REFRESH_LAYOUT_VERSION = "1.2.0-alpha01"
_COMPOSE_VERSION = "1.0.0-alpha10"
_DATA_STORE_VERSION = "1.0.0-alpha06"
_DATABINDING_VERSION = "7.0.0-alpha05"
_ACCOMPANIST_VERSION = "0.4.2"
_LOTTIE_VERSION = "3.4.2"
_RETROFIT_VERSION = "2.9.0"
_OK_HTTP_LOGGING_INTERCEPTOR_VERSION = "4.7.2"
_APOLLO_VERSION = "2.4.6"
_COMMON_MARK_VERSION = "0.15.2"
_JSOUP_VERSION = "1.12.1"
_TIMBER_VERSION = "4.7.1"
_FIREBASE_ANALYTICS_KTX_VERSION = "17.5.0"
_FIREBASE_CRASHLYTICS_VERSION = "17.2.1"
_INSETTER_VERSION = "0.3.1"
_MOSHI_VERSION = "1.9.2"
_PROTOC_VERSION = "3.14.0"
_PROTOBUF_JAVA_LITE_VERSION = "3.11.0"
_JUNIT_VERSION = "4.13"
_ANDROID_JUNIT_VERSION = "1.1.2"
_ANDROID_TEST_CORE_VERSION = "1.3.0"
_ANDROID_TEST_RUNNER_VERSION = "1.3.0"
_ANDROID_TEST_RULES_VERSION = "1.3.0"
_ESPRESSO_VERSION = "3.3.0"
_HAMCREST_VERSION = "1.3"

maven_install(
    artifacts = [
        "org.jetbrains.kotlin:kotlin-stdlib:{}".format(_KOTLIN_COMPILER_VERSION),
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:{}".format(_KOTLIN_COROUTINES_VERSION),
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:{}".format(_KOTLIN_COROUTINES_VERSION),
        "org.jetbrains.kotlinx:kotlinx-datetime:{}".format(_KOTLIN_DATETIME_VERSION),
        "org.jsoup:jsoup:{}".format(_JSOUP_VERSION),
        "com.jakewharton.timber:timber:{}".format(_TIMBER_VERSION),
        "androidx.constraintlayout:constraintlayout:{}".format(_CONSTRAINT_LAYOUT_VERSION),
        "androidx.coordinatorlayout:coordinatorlayout:{}".format(_COORDINATOR_LAYOUT_VERSION),
        "androidx.swiperefreshlayout:swiperefreshlayout:{}".format(_SWIPE_REFRESH_LAYOUT_VERSION),
        "androidx.viewpager2:viewpager2:{}".format(_VIEWPAGER2_VERSION),
        "androidx.browser:browser:{}".format(_BROWSER_VERSION),
        "androidx.appcompat:appcompat:{}".format(_APPCOMPAT_VERSION),
        "androidx.fragment:fragment-ktx:{}".format(_FRAGMENT_VERSION),
        "androidx.drawerlayout:drawerlayout:{}".format(_DRAWER_LAYOUT_VERSION),
        "androidx.recyclerview:recyclerview:{}".format(_RECYCLER_VIEW_VERSION),
        "androidx.recyclerview:recyclerview-selection:{}".format(_RECYCLER_VIEW_SELECTION_VERSION),
        "androidx.datastore:datastore:{}".format(_DATA_STORE_VERSION),
        "androidx.databinding:databinding-common:{}".format(_DATABINDING_VERSION),
        "androidx.databinding:databinding-adapters:{}".format(_DATABINDING_VERSION),
        "androidx.databinding:databinding-runtime:{}".format(_DATABINDING_VERSION),
        "androidx.lifecycle:lifecycle-extensions:{}".format(_LIFECYCLE_EXTENSIONS_VERSION),
        "androidx.lifecycle:lifecycle-livedata-ktx:{}".format(_LIVE_DATA_KTX_VERSION),
        "androidx.navigation:navigation-fragment-ktx:{}".format(_NAVIGATION_VERSION),
        "androidx.navigation:navigation-ui-ktx:{}".format(_NAVIGATION_VERSION),
        "androidx.navigation:navigation-compose:{}".format(_NAVIGATION_COMPOSE_VERSION),
        "androidx.paging:paging-common-ktx:{}".format(_PAGING_VERSION),
        "androidx.paging:paging-runtime-ktx:{}".format(_PAGING_VERSION),
        "androidx.paging:paging-compose:{}".format(_PAGING_COMPOSE_VERSION),
        "androidx.room:room-common:{}".format(_ROOM_VERSION),
        "androidx.room:room-compiler:{}".format(_ROOM_VERSION),
        "androidx.room:room-runtime:{}".format(_ROOM_VERSION),
        "androidx.room:room-migration:{}".format(_ROOM_VERSION),
        "androidx.room:room-ktx:{}".format(_ROOM_VERSION),
        "androidx.work:work-runtime-ktx:{}".format(_WORK_MANAGER_VERSION),
        "androidx.compose.runtime:runtime:{}".format(_COMPOSE_VERSION),
        "androidx.compose.ui:ui:{}".format(_COMPOSE_VERSION),
        "androidx.compose.foundation:foundation-layout:{}".format(_COMPOSE_VERSION),
        "androidx.compose.material:material:{}".format(_COMPOSE_VERSION),
        "androidx.compose.foundation:foundation:{}".format(_COMPOSE_VERSION),
        "androidx.compose.animation:animation:{}".format(_COMPOSE_VERSION),
        "androidx.compose.ui:ui-tooling:{}".format(_COMPOSE_VERSION),
        "androidx.compose.runtime:runtime-livedata:{}".format(_COMPOSE_VERSION),
        "androidx.compose.compiler:compiler:{}".format(_COMPOSE_VERSION),
        "com.google.android.material:material:{}".format(_MATERIAL_VERSION),
        "com.google.android.material:compose-theme-adapter:{}".format(_COMPOSE_VERSION),
        "com.google.firebase:firebase-analytics-ktx:{}".format(_FIREBASE_ANALYTICS_KTX_VERSION),
        "com.google.firebase:firebase-crashlytics:{}".format(_FIREBASE_CRASHLYTICS_VERSION),
        "com.google.protobuf:protoc:{}".format(_PROTOC_VERSION),
        "com.google.protobuf:protobuf-javalite:{}".format(_PROTOBUF_JAVA_LITE_VERSION),
        "com.squareup.retrofit2:retrofit:{}".format(_RETROFIT_VERSION),
        "com.squareup.retrofit2:converter-moshi:{}".format(_RETROFIT_VERSION),
        "com.squareup.okhttp3:logging-interceptor:{}".format(_OK_HTTP_LOGGING_INTERCEPTOR_VERSION),
        "com.apollographql.apollo:apollo-runtime:{}".format(_APOLLO_VERSION),
        "com.airbnb.android:lottie:{}".format(_LOTTIE_VERSION),
        "dev.chrisbanes.accompanist:accompanist-coil:{}".format(_ACCOMPANIST_VERSION),
        "dev.chrisbanes.accompanist:accompanist-insets:{}".format(_ACCOMPANIST_VERSION),
        "com.atlassian.commonmark:commonmark:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-autolink:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-gfm-strikethrough:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-gfm-tables:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-ins:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-heading-anchor:{}".format(_COMMON_MARK_VERSION),
        "com.atlassian.commonmark:commonmark-ext-yaml-front-matter:{}".format(_COMMON_MARK_VERSION),
        "dev.chrisbanes:insetter-dbx:{}".format(_INSETTER_VERSION),
        "dev.chrisbanes:insetter-ktx:{}".format(_INSETTER_VERSION),
        "com.squareup.moshi:moshi-kotlin-codegen:{}".format(_MOSHI_VERSION),
        "com.squareup.moshi:moshi-adapters:{}".format(_MOSHI_VERSION),
        "junit:junit:{}".format(_JUNIT_VERSION),
        "org.hamcrest:hamcrest-all:{}".format(_HAMCREST_VERSION),
        "com.squareup.retrofit2:retrofit-mock:{}".format(_RETROFIT_VERSION),
        "androidx.test:core-ktx:{}".format(_ANDROID_TEST_CORE_VERSION),
        "androidx.test:runner:{}".format(_ANDROID_TEST_RUNNER_VERSION),
        "androidx.test:rules:{}".format(_ANDROID_TEST_RULES_VERSION),
        "androidx.test.ext:junit-ktx:{}".format(_ANDROID_JUNIT_VERSION),
        "androidx.test.espresso:espresso-core:{}".format(_ESPRESSO_VERSION),
        "androidx.test.espresso:espresso-contrib:{}".format(_ESPRESSO_VERSION),
        "androidx.test.espresso:espresso-intents:{}".format(_ESPRESSO_VERSION),
        "androidx.test.espresso:espresso-accessibility:{}".format(_ESPRESSO_VERSION),
        "androidx.work:work-testing:{}".format(_WORK_MANAGER_VERSION),
        "androidx.room:room-testing:{}".format(_ROOM_VERSION),
        "androidx.fragment:fragment-testing:{}".format(_FRAGMENT_VERSION),
        "androidx.compose.ui:ui-test:{}".format(_COMPOSE_VERSION),
        "androidx.compose.ui:ui-test-junit4:{}".format(_COMPOSE_VERSION),
    ],
    repositories = [
        "https://bintray.com/bintray/jcenter",
        "https://jcenter.bintray.com/",
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
        "https://kotlin.bintray.com/kotlinx/",
        "http://dl.bintray.com/kotlin/kotlin-eap",
        "https://oss.sonatype.org/content/repositories/snapshots/",
    ],
    fetch_sources = True,
    version_conflict_policy = "pinned",
)

## Stardoc

_STARDOC_VERSION = "0.4.0"

_STARDOC_SHA = "36b8d6c2260068b9ff82faea2f7add164bf3436eac9ba3ec14809f335346d66a"

http_archive(
    name = "io_bazel_stardoc",
    sha256 = _STARDOC_SHA,
    strip_prefix = "stardoc-{}".format(_STARDOC_VERSION),
    urls = [
        "https://github.com/bazelbuild/stardoc/archive/{}.zip".format(_STARDOC_VERSION),
    ],
)

load("@io_bazel_stardoc//:setup.bzl", "stardoc_repositories")

stardoc_repositories()

## Import Skylib

_SKYLIB_VERSION = "1.0.2"

_SKYLIB_SHA = "97e70364e9249702246c0e9444bccdc4b847bed1eb03c5a3ece4f83dfe6abc44"

http_archive(
    name = "bazel_skylib",
    sha256 = _SKYLIB_SHA,
    urls = [
        "https://github.com/bazelbuild/bazel-skylib/releases/download/{0}/bazel-skylib-{0}.tar.gz".format(_SKYLIB_VERSION),
    ],
)

load("@bazel_skylib//:workspace.bzl", "bazel_skylib_workspace")

bazel_skylib_workspace()

## Protobuf

_PROTOBUF_VERSION = "3.14.0"

_PROTOBUF_SHA = "bf0e5070b4b99240183b29df78155eee335885e53a8af8683964579c214ad301"

http_archive(
    name = "com_google_protobuf",
    sha256 = _PROTOBUF_SHA,
    strip_prefix = "protobuf-{}".format(_PROTOBUF_VERSION),
    urls = [
        "https://github.com/protocolbuffers/protobuf/archive/v{}.zip".format(_PROTOBUF_VERSION),
    ],
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")

protobuf_deps()

## Rules PKG

_RULES_PKG_VERSION = "0.2.4"

_RULES_PKG_SHA = "4ba8f4ab0ff85f2484287ab06c0d871dcb31cc54d439457d28fd4ae14b18450a"

http_archive(
    name = "rules_pkg",
    sha256 = _RULES_PKG_SHA,
    urls = [
        "https://github.com/bazelbuild/rules_pkg/releases/download/{0}/rules_pkg-{0}.tar.gz".format(_RULES_PKG_VERSION),
    ],
)

## Android

_RULES_ANDROID_VERSION = "0.1.1"

_RULES_ANDROID_SHA = "cd06d15dd8bb59926e4d65f9003bfc20f9da4b2519985c27e190cddc8b7a7806"

http_archive(
    name = "build_bazel_rules_android",
    sha256 = _RULES_ANDROID_SHA,
    strip_prefix = "rules_android-{}".format(_RULES_ANDROID_VERSION),
    urls = [
        "https://github.com/bazelbuild/rules_android/archive/v{}.zip".format(_RULES_ANDROID_VERSION),
    ],
)

load("@build_bazel_rules_android//android:rules.bzl", "android_sdk_repository")

android_sdk_repository(
    name = "androidsdk",
    api_level = 30,
)

## Kotlin

_RULES_KOTLIN_VERSION = "6d0a72d634c9ba2ae26e2af4927500a6739d3acd"

_RULES_KOTLIN_SHA = "600c8d07451bdc85e599180f172e5625cd6afe1eea80dc5aaaa109f5517c07bc"

http_archive(
    name = "io_bazel_rules_kotlin",
    sha256 = _RULES_KOTLIN_SHA,
    strip_prefix = "rules_kotlin-{}".format(_RULES_KOTLIN_VERSION),
    urls = [
        "https://github.com/lyft/rules_kotlin/archive/{}.tar.gz".format(_RULES_KOTLIN_VERSION),
    ],
)
load("@io_bazel_rules_kotlin//kotlin:dependencies.bzl", "kt_download_local_dev_dependencies")

kt_download_local_dev_dependencies()

load("@io_bazel_rules_kotlin//kotlin:kotlin.bzl", "kotlin_repositories", "kt_register_toolchains")

_RULES_KOTLIN_COMPILER_RELEASE = {
    "urls": [
        "https://github.com/JetBrains/kotlin/releases/download/v{v}/kotlin-compiler-{v}.zip".format(v = _KOTLIN_COMPILER_VERSION),
    ],
    "sha256": "46720991a716e90bfc0cf3f2c81b2bd735c14f4ea6a5064c488e04fd76e6b6c7",
}

kotlin_repositories()

register_toolchains("//:kotlin_toolchain")

load("@bazel_tools//tools/build_defs/repo:git.bzl", "git_repository")

# Add support for Firebase Crashlytics
git_repository(
    name = "tools_android",
    commit = "00e6f4b7bdd75911e33c618a9bc57bab7a6e8930",
    remote = "https://github.com/bazelbuild/tools_android",
)

load("@tools_android//tools/googleservices:defs.bzl", "google_services_workspace_dependencies")

google_services_workspace_dependencies()

bind(
    name = "databinding_annotation_processor",
    actual = "//tools/android:compiler_annotation_processor",
)