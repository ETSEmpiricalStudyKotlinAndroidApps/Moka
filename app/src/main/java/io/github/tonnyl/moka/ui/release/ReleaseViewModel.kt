package io.github.tonnyl.moka.ui.release

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.fragment.Release
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.ReleaseQuery
import io.github.tonnyl.moka.util.HtmlHandler
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
                ).data?.repository?.release

                _release.postValue(
                    Resource.success(
                        data = if (resp?.descriptionHTML.isNullOrEmpty()) {
                            null
                        } else {
                            resp?.copy(
                                descriptionHTML = HtmlHandler.basicHtmlTemplate(
                                    cssPath = "./github_release_light.css",
                                    body = resp.descriptionHTML!!
                                )
                            )
                        }
                    )
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _release.postValue(Resource.error(e.message, release.value?.data))
            }
        }
    }

}