package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.FileContentQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.wrapWithHtmlTemplate
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import timber.log.Timber
import java.util.*

class RepositoryReadmeFileLiveData(
        private val login: String,
        private val name: String,
        private val expression: String
) : LiveData<Resource<String>>() {

    private val call = NetworkClient.apolloClient
            .query(FileContentQuery.builder()
                    .login(login)
                    .repoName(name)
                    .expression(expression)
                    .build())

    init {
        refresh()
    }

    override fun onInactive() {
        super.onInactive()

        if (!call.isCanceled) {
            call.cancel()
        }
    }

    fun refresh() {
        value = Resource.loading(null)

        call.enqueue(object : ApolloCall.Callback<FileContentQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<FileContentQuery.Data>) {
                // todo do not parse on main thread

                val extensions = Arrays.asList(
                        TablesExtension.create(),
                        AutolinkExtension.create(),
                        StrikethroughExtension.create(),
                        InsExtension.create(),
                        YamlFrontMatterExtension.create())
                val parser = Parser.builder()
                        .extensions(extensions)
                        .build()
                val document = parser.parse(response.data()?.repository()?.`object`()?.fragments()?.fileTextAbstract()?.text())
                val renderer = HtmlRenderer.builder()
                        .extensions(extensions)
                        .escapeHtml(false)
                        .build()
                val html = wrapWithHtmlTemplate(renderer.render(document), login, name, expression)

                postValue(Resource.success(html))
            }

        })
    }

}