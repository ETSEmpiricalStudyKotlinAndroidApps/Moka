package io.tonnyl.moka.common

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import io.ktor.client.*
import io.tonnyl.moka.common.db.MokaDataBase
import io.tonnyl.moka.common.network.ApolloGraphQLClient
import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.network.api.*
import io.tonnyl.moka.common.store.ContributionCalendarSerializer
import io.tonnyl.moka.common.store.EmojiSerializer
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ContributionCalendar
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.RecentEmojis
import io.tonnyl.moka.common.store.data.SignedInAccount
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
actual class AccountInstance(
    application: Application,
    private val unauthenticatedKtorClient: HttpClient,
    val signedInAccount: SignedInAccount,
) {

    private val authenticatedKtorClient = KtorClient(
        requireAuth = true,
        accessToken = signedInAccount.accessToken.accessToken
    ).httpClient

    actual val eventApi = EventApi(ktorClient = authenticatedKtorClient)
    actual val trendingApi = TrendingApi(ktorClient = unauthenticatedKtorClient)
    actual val userApi = UserApi(ktorClient = authenticatedKtorClient)
    actual val notificationApi = NotificationApi(ktorClient = authenticatedKtorClient)
    actual val commitApi = CommitApi(ktorClient = unauthenticatedKtorClient)
    actual val repositoryContentApi = RepositoryContentApi(ktorClient = unauthenticatedKtorClient)
    actual val repositoryApi = RepositoryApi(
        authenticatedKtorClient = authenticatedKtorClient,
        unauthenticatedKtorClient = unauthenticatedKtorClient
    )

    val database = MokaDataBase.getInstance(
        context = application,
        userId = signedInAccount.account.id
    )

    actual val apolloGraphQLClient =
        ApolloGraphQLClient(accessToken = signedInAccount.accessToken.accessToken)

    val recentEmojisDataStore: DataStore<RecentEmojis> by lazy { application.recentEmojisDataStore }

    val exploreOptionsDataStore: DataStore<ExploreOptions> by lazy { application.exploreOptionsDataStore }

    val contributionCalendarDataStore: DataStore<ContributionCalendar> by lazy { application.contributionCalendarDataStore }

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

    @ExperimentalSerializationApi
    private val Context.contributionCalendarDataStore: DataStore<ContributionCalendar> by dataStore(
        fileName = "${signedInAccount.account.id}_contribution_calendar.pb",
        serializer = ContributionCalendarSerializer
    )

}