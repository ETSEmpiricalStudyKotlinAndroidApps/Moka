# Moka

[![Build status](https://github.com/TonnyL/Moka/workflows/build/badge.svg)](https://github.com/TonnyL/Moka/actions?query=workflow%3Abuild)

An Android app for github.com.

## Feature
+ Entirely written in [Kotlin](https://kotlinlang.org/)
+ Uses [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
+ Uses [Architecture Components](https://developer.android.com/topic/libraries/architecture/), including LiveData, Navigation, Paging, Room, ViewModel and Work Manager
+ Uses [apollo-android](https://github.com/apollographql/apollo-android) for working with [GitHub's GraphQL API v4](https://developer.github.com/v4/)

## Development Setup
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

## LICENSE
Moka is under an MIT license. See the [LICENSE](LICENSE) file for more information.