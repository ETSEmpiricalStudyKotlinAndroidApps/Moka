package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.FileContentQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.data.Resource
import io.github.tonnyl.moka.data.Status
import io.github.tonnyl.moka.util.wrapWithHtmlTemplate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    private val TAG = javaClass.simpleName

    private val call = NetworkClient.apolloClient
            .query(FileContentQuery.builder().login(login).repoName(name).expression(expression).build())
            .httpCachePolicy(HttpCachePolicy.CACHE_FIRST)
            .watcher()

    private val disposable = Rx2Apollo.from(call)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { response ->
                if (response.hasErrors()) {
                    Resource(Status.ERROR, null, response.errors().joinToString())
                } else {
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

                    Resource(Status.SUCCESS, html, null)
                }
            }
            .subscribe({
                this.value = it
            }, {
                Timber.e(it, "fetchReadmeContent error: ${it.message}")
            })

    override fun onInactive() {
        super.onInactive()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}