package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.FileContentQuery
import io.github.tonnyl.moka.RepositoryQuery
import io.github.tonnyl.moka.data.RepositoryGraphQL
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.util.execute
import io.github.tonnyl.moka.util.wrapWithHtmlTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import timber.log.Timber

class RepositoryViewModel(
    private val login: String,
    private val repositoryName: String
) : ViewModel() {

    private val _repositoryResult = MutableLiveData<Resource<RepositoryGraphQL>>()
    val repositoryResult: LiveData<Resource<RepositoryGraphQL>>
        get() = _repositoryResult

    private val _readmeFile = MutableLiveData<Resource<String>>()
    val readmeFile: LiveData<Resource<String>>
        get() = _readmeFile

    private val _readmeFileName = MutableLiveData<Resource<Pair<String, String>>>()
    val readmeFileName: LiveData<Resource<Pair<String, String>>>
        get() = _readmeFileName

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _repositoryResult.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            RepositoryQuery.builder()
                                .login(login)
                                .repoName(repositoryName)
                                .build()
                        )
                        .execute()
                }

                _repositoryResult.postValue(
                    Resource.success(RepositoryGraphQL.createFromRaw(response.data()))
                )
            } catch (e: Exception) {
                Timber.e(e)

                _repositoryResult.postValue(Resource.error(e.message, null))
            }

        }
    }

    fun updateExpression(expression: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _readmeFile.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            FileContentQuery.builder()
                                .login(login)
                                .repoName(repositoryName)
                                .expression(expression)
                                .build()
                        )
                        .execute()
                }

                val extensions = listOf(
                    TablesExtension.create(),
                    AutolinkExtension.create(),
                    StrikethroughExtension.create(),
                    InsExtension.create(),
                    YamlFrontMatterExtension.create()
                )
                val parser = Parser.builder()
                    .extensions(extensions)
                    .build()
                val document =
                    parser.parse(response.data()?.repository()?.`object`()?.fragments()?.fileTextAbstract()?.text())
                val renderer = HtmlRenderer.builder()
                    .extensions(extensions)
                    .escapeHtml(false)
                    .build()
                val html = wrapWithHtmlTemplate(
                    renderer.render(document),
                    login,
                    repositoryName,
                    expression
                )

                _readmeFile.postValue(Resource.success(html))
            } catch (e: Exception) {
                Timber.e(e)

                _readmeFile.postValue(Resource.error(e.message, null))
            }
        }
    }

    fun updateBranchName(branchName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _readmeFileName.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            CurrentLevelTreeViewQuery.builder()
                                .login(login)
                                .repoName(repositoryName)
                                .expression("$branchName:")
                                .build()
                        )
                        .execute()
                }

                val readmeFiles = response.data()
                    ?.repository()
                    ?.`object`()
                    ?.fragments()
                    ?.treeAbstract()
                    ?.entries()
                    ?.filter {
                        it.name().toLowerCase().contains("readme")
                    }

                if (readmeFiles.isNullOrEmpty()) {
                    Resource(Status.SUCCESS, null, null)
                } else {
                    val mdIndex = readmeFiles.indexOfFirst { it.name().endsWith(".md") }
                    _readmeFileName.postValue(
                        if (mdIndex >= 0) {
                            Resource.success(Pair("md", readmeFiles[mdIndex].name()))
                        } else {
                            val htmlIndex = readmeFiles.indexOfFirst { it.name().endsWith(".html") }
                            if (htmlIndex >= 0) {
                                Resource.success(Pair("html", readmeFiles[htmlIndex].name()))
                            } else {
                                val plainIndex =
                                    readmeFiles.indexOfFirst { it.name().toLowerCase() == "readme" }
                                if (plainIndex >= 0) {
                                    Resource.success(Pair("plain", readmeFiles[plainIndex].name()))
                                } else {
                                    Resource.success(null)
                                }
                            }
                        })
                }
            } catch (e: Exception) {
                Timber.e(e)

                _readmeFileName.postValue(Resource.error(e.message, null))
            }
        }
    }

}