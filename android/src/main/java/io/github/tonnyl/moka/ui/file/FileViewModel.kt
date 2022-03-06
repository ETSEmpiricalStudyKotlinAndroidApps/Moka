package io.github.tonnyl.moka.ui.file

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.HighlightLanguage
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.serialization.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import okio.buffer
import okio.source

@ExperimentalSerializationApi
data class FileViewModelExtra(
    val accountInstance: AccountInstance,
    val url: String,
    val filename: String,
    val fileExtension: String?
)

@ExperimentalPagingApi
@ExperimentalSerializationApi
class FileViewModel(
    app: Application,
    private val extra: FileViewModelExtra
) : AndroidViewModel(app) {

    private val _file = MutableLiveData<Resource<Pair<String, String?>>>()
    val file: LiveData<Resource<Pair<String, String?>>>
        get() = _file

    private val _redirectedUrl = MutableLiveData<Resource<String>?>()
    val redirectedUrl: LiveData<Resource<String>?>
        get() = _redirectedUrl

    init {
        geFileContent()
    }

    fun geFileContent() {
        viewModelScope.launch {
            try {
                _file.value = Resource.loading(data = null)

                val result = withContext(Dispatchers.IO) {
                    val fileContent =
                        extra.accountInstance.repositoryContentApi.getFile(url = extra.url)

                    // highlight.js' language auto-detection is not accurate enough,
                    // so we need a way to improve.
                    val result =
                        getApplication<MokaApp>().assets.open("highlight/highlight-languages.json")
                            .use { inputStream ->
                                val jsonString =
                                    inputStream.source().buffer().readString(Charsets.UTF_8)
                                json.decodeFromString<List<HighlightLanguage>>(jsonString)
                            }

                    val language = result.firstOrNull { highlightLanguage ->
                        highlightLanguage.filenames?.firstOrNull { it.endsWith(extra.filename) } != null
                                || (extra.fileExtension != null && highlightLanguage.extensions.contains(
                            extra.fileExtension
                        ))
                    }

                    Pair(fileContent, language?.lang)
                }

                _file.value = Resource.success(data = result)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    "failed to get code lines: ${e.asLog()}"
                }

                _file.value = Resource.error(exception = e, data = null)
            }
        }
    }

    fun getRedirectedUrl(url: String) {
        if (redirectedUrl.value?.status == Status.LOADING) {
            return
        }

        viewModelScope.launch {
            try {
                _redirectedUrl.value = Resource.loading(data = null)

                val result = extra.accountInstance.repositoryContentApi.getRedirectedUrl(url = url)

                _redirectedUrl.value = Resource.success(data = result)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) {
                    e.asLog()
                }

                _redirectedUrl.value = Resource.error(exception = e, data = null)
            }
        }
    }

    fun onRedirectedDataErrorDismissed() {
        _redirectedUrl.value = null
    }

}