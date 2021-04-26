package io.github.tonnyl.moka

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.ApolloGraphQLClient
import io.github.tonnyl.moka.network.KtorClient
import io.github.tonnyl.moka.network.api.EventApi
import io.github.tonnyl.moka.network.api.NotificationApi
import io.github.tonnyl.moka.network.api.TrendingApi
import io.github.tonnyl.moka.network.api.UserApi
import io.github.tonnyl.moka.proto.RecentEmojis
import io.github.tonnyl.moka.proto.SignedInAccount
import io.github.tonnyl.moka.serializers.store.EmojiSerializer

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

    val database = MokaDataBase.getInstance(
        context = app,
        userId = signedInAccount.account.id
    )

    val apolloGraphQLClient =
        ApolloGraphQLClient(accessToken = signedInAccount.accessToken.accessToken)

    val recentEmojisDataStore: DataStore<RecentEmojis> = app.recentEmojisDataStore

}

private val Context.recentEmojisDataStore: DataStore<RecentEmojis> by dataStore(
    fileName = "recent_emojis.pb",
    serializer = EmojiSerializer
)