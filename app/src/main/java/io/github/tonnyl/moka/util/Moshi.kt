package io.github.tonnyl.moka.util

import android.net.Uri
import com.squareup.moshi.*
import com.squareup.moshi.adapters.EnumJsonAdapter
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.type.ProjectState
import io.github.tonnyl.moka.ui.explore.filters.LocalLanguage
import java.util.*

object MoshiInstance {

    val eventGollumPageListAdapter = EventGollumPageListAdapter()
    val uriAdapter = UriAdapter()

    val localLanguageListAdapter: JsonAdapter<List<LocalLanguage>> by lazy {
        val type = Types.newParameterizedType(List::class.java, LocalLanguage::class.java)
        moshi.adapter<List<LocalLanguage>>(type)
    }

    val emojiListAdapter: JsonAdapter<List<Emoji>> by lazy {
        val type = Types.newParameterizedType(List::class.java, Emoji::class.java)
        moshi.adapter<List<Emoji>>(type)
    }

    val eventGistFileMapAdapter: JsonAdapter<Map<String, EventGistFile>> by lazy {
        val type = Types.newParameterizedType(
            Map::class.java,
            String::class.java,
            EventGistFile::class.java
        )

        moshi.adapter<Map<String, EventGistFile>>(type)
    }

    val trendingRepositoryBuiltByListAdapter: JsonAdapter<List<TrendingRepositoryBuiltBy>> by lazy {
        val type = Types.newParameterizedType(
            List::class.java,
            TrendingRepositoryBuiltBy::class.java
        )
        moshi.adapter<List<TrendingRepositoryBuiltBy>>(type)
    }

    val authenticatedUserAdapter by lazy {
        moshi.adapter(AuthenticatedUser::class.java)
    }

    val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(eventGollumPageListAdapter)
            .add(uriAdapter)
            .add(Iso8601DateAdapter())
            .add(
                ProjectState::class.java,
                EnumJsonAdapter.create(ProjectState::class.java)
                    .withUnknownFallback(ProjectState.UNKNOWN__)
            )
            .add(
                NotificationReasons::class.java,
                EnumJsonAdapter.create(NotificationReasons::class.java)
                    .withUnknownFallback(NotificationReasons.OTHER)
            )
            .build()
    }

}

class Iso8601DateAdapter {

    @ToJson
    fun toJson(value: Date): String {
        return Iso8601Utils.format(value)
    }

    @FromJson
    fun fromJson(value: String): Date {
        return Iso8601Utils.parse(value)
    }

}

class EventGollumPageListAdapter {

    private val adapter by lazy {
        val type = Types.newParameterizedType(List::class.java, EventGollumPage::class.java)
        MoshiInstance.moshi.adapter<List<EventGollumPage>>(type)
    }

    @ToJson
    fun toJson(pages: List<EventGollumPage>): String {
        return adapter.nullSafe().toJson(pages)
    }

    @FromJson
    fun fromJson(json: String): List<EventGollumPage> {
        return adapter.fromJson(json) ?: emptyList()
    }

}

class UriAdapter {

    @ToJson
    fun toJson(uri: Uri): String {
        return uri.toString()
    }

    @FromJson
    fun fromJson(json: String): Uri {
        return Uri.parse(json)
    }

}