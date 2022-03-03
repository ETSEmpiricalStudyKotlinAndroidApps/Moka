# Moka

[![Android Build status](https://github.com/TonnyL/Moka/workflows/Android/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Aandroid)
[![Android Build status](https://github.com/TonnyL/Moka/workflows/wearOS/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Awearos)
[![iOS Build status](https://github.com/TonnyL/Moka/workflows/iOS/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Aios)

A GitHub app, for Android and iOS.

## Feature

+ Share some common code via [Kotlin Multiplatform (Mobile)](https://kotlinlang.org/docs/multiplatform.html):
	- Network layer([Ktor](https://ktor.io/) for REST APIs and [apollo-kotlin](https://github.com/apollographql/apollo-kotlin) for GraphQL APIs, [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for serializations);
	- Data layer(Sort of. Basic model classes.).
+ On Android side:
	- 100% [Kotlin](https://kotlinlang.org/);
   - The whole ui is written with [Jetpack Compose](https://developer.android.com/jetpack/compose);
   - built with the latest development technologies, including [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) and [Android Jetpack](https://developer.android.com/jetpack);
   - supports [WearOS](https://developer.android.com/wear).
+ On iOS Side:
    - 100% [Swift](https://developer.apple.com/swift/);
    - The whole ui is written with [SwiftUI](https://developer.apple.com/xcode/swiftui/).

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
ANDROID_CLIENT_ID=Your Android client id
ANDROID_CLIENT_SECRET=Your Android client secret
IOS_CLIENT_ID=Your iOS client id
IOS_CLIENT_SECRET=Your iOS client secret
STORE_FILE_PATH=Path to key store file
STORE_PASSWORD=Your key store password
KEY_ALIAS=Your key alias
KEY_PASSWORD=Your key password
```

For the callback URL, you can define your owns or use `moka-app://callback` for working out of the box.

### App Versions

Create a `version.properties` file in project root directory with code below:

```java
versionCode=Your code version // Must be an integer.
versionName=Your code version name // Must be in Semantic Versioning format, like 1.0.0.
```

### Xcode Config
Implementing [Cocoapods](https://cocoapods.org/) for just connecting one framework to the iOS project seems like overkill, we'll do it manually instead.

1. In Xcode, open the iOS project settings by double-clicking the project name;
2. On the **Build Phases** tab of the project settings, click the **+** and add **New Run Script Phase**;
3. Add the following script:

	```shell
	cd "$SRCROOT/.."
	./gradlew :common:embedAndSignAppleFrameworkForXcode
	```

4. Move the **Run Script** phase before the **Compile Sources** phase;
5. On the **Build Settings** tab, specify the **Framework Search Path** under **Search Paths**:
	
	```shell
	$(SRCROOT)/../common/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)
	```

6. On the **Build Settings** tab, specify the **Other Linker flags** under **Linking**:
	
	```shell
	$(inherited) -framework common
	```

7. Build the project in Xcode. 

## Release
In project root directory:

```shell
% cd ./scripts && sh release-android.sh // or release-ios.sh, release-all.sh, etc.
```

## License
Moka is under an MIT license. See the [LICENSE](LICENSE) file for more information.
