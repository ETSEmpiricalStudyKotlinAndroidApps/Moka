package io.github.tonnyl.moka.ui.release

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.util.HtmlHandler
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.graphql.ReleaseQuery
import io.tonnyl.moka.graphql.fragment.Release
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
class ReleaseViewModel(
    private val accountInstance: AccountInstance,
    private val login: String,
    private val repoName: String,
    private val tagName: String
) : ViewModel() {

    private val _release = MutableLiveData<Resource<Release>>()
    val release: LiveData<Resource<Release>>
        get() = _release

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _release.postValue(Resource.loading(release.value?.data))

                val resp = accountInstance.apolloGraphQLClient.apolloClient.query(
                    query = ReleaseQuery(
                        login = login,
                        repoName = repoName,
                        tagName = tagName
                    )
                ).execute().data?.repository?.release

                _release.postValue(
                    Resource.success(
                        data = resp?.release?.copy(
                            descriptionHTML = HtmlHandler.basicHtmlTemplate(
                                cssPath = "./github_release_light.css",
                                body = resp.release.descriptionHTML ?: ""
                            )
                        )
                    )
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _release.postValue(Resource.error(e, release.value?.data))
            }
        }
    }

}