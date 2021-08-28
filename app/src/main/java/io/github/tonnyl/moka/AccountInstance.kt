package io.github.tonnyl.moka

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.ApolloGraphQLClient
import io.github.tonnyl.moka.network.KtorClient
import io.github.tonnyl.moka.network.api.*
import io.github.tonnyl.moka.serializers.store.EmojiSerializer
import io.github.tonnyl.moka.serializers.store.ExploreOptionsSerializer
import io.github.tonnyl.moka.serializers.store.data.ExploreOptions
import io.github.tonnyl.moka.serializers.store.data.RecentEmojis
import io.github.tonnyl.moka.serializers.store.data.SignedInAccount
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class AccountInstance(
    app: MokaApp,
    val signedInAccount: SignedInAccount
) {

    private val unauthenticatedKtorClient = app.unauthenticatedKtorClient

    private val authenticatedKtorClient = KtorClient(
        context = app,
        requireAuth = true,
        accessToken = signedInAccount.accessToken.accessToken
    ).httpClient

    val eventApi = EventApi(ktorClient = authenticatedKtorClient)
    val trendingApi = TrendingApi(ktorClient = unauthenticatedKtorClient)
    val userApi = UserApi(ktorClient = authenticatedKtorClient)
    val notificationApi = NotificationApi(ktorClient = authenticatedKtorClient)
    val commitApi = CommitApi(ktorClient = unauthenticatedKtorClient)

    val database = MokaDataBase.getInstance(
        context = app,
        userId = signedInAccount.account.id
    )

    val apolloGraphQLClient =
        ApolloGraphQLClient(accessToken = signedInAccount.accessToken.accessToken)

    val recentEmojisDataStore: DataStore<RecentEmojis> by lazy { app.recentEmojisDataStore }

    val exploreOptionsDataStore: DataStore<ExploreOptions> by lazy { app.exploreOptionsDataStore }

    @ExperimentalSerializationApi
    private val Context.recentEmojisDataStore: DataStore<RecentEmojis> by dataStore(
        fileName = "${signedInAccount.account.id}_recent_emojis.pb",
        serializer = EmojiSerializer
    )

    @ExperimentalSerializationApi
    private val Context.exploreOptionsDataStore: DataStore<ExploreOptions> by dataStore(
        fileName = "${signedInAccount.account.id}_explore_options.pb",
        serializer = ExploreOptionsSerializer
    )

}