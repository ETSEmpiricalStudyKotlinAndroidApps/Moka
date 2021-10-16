# Moka

[![Android Build status](https://github.com/TonnyL/Moka/workflows/Android/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Aandroid)
[![iOS Build status](https://github.com/TonnyL/Moka/workflows/iOS/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Aios)

An Android app for github.com.

## Feature

+ Entirely written in [Kotlin](https://kotlinlang.org/)
+ Uses [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
+ Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/),
  including LiveData, Navigation, Paging, Room, ViewModel and Work Manager
+ Uses [apollo-android](https://github.com/apollographql/apollo-android) for working
  with [GitHub's GraphQL API v4](https://developer.github.com/v4/)

## Environment setup

### Requirements

+ Install [Android Studio](https://developer.android.com/studio) version Arctic Fox (2020.3.1) or
  higher;
+ Install [Xcode](https://developer.apple.com/xcode/) version 13.0 or higher;
+ Install [JDK](https://www.oracle.com/java/technologies/javase-downloads.html) version 11 or
  higher;
+ Update Kotlin plugin to latest stable version in Android Studio;
+ Install [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
in Android Studio.

### Clone the Repository

```shell
git clone https://github.com/TonnyL/Moka.git
```

### API Keys

Register a new OAuth application on [GitHub](https://github.com/settings/applications/new).

After obtained your keys, place them into `local.properties` file.

```java
CLIENT_ID=YOUR CLIENT ID
CLIENT_SECRET=YOUR CLIENT SECRET
```

For the callback URL, you can define your owns or use `moka-app://callback` for working out of the box.

## License
Moka is under an MIT license. See the [LICENSE](LICENSE) file for more information.
