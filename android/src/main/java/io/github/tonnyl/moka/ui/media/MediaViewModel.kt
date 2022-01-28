package io.github.tonnyl.moka.ui.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import io.github.tonnyl.moka.util.md5
import io.github.tonnyl.moka.work.DownloadMediaWorker
import io.github.tonnyl.moka.work.SaveMediaWorker
import io.github.tonnyl.moka.work.ShareMediaWorker
import io.tonnyl.moka.common.AccountInstance
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class MediaViewModelExtra(
    val accountInstance: AccountInstance,
    val url: String,
    val filename: String
)

@ExperimentalPagingApi
@ExperimentalSerializationApi
class MediaViewModel(
    app: Application,
    private val extra: MediaViewModelExtra
) : AndroidViewModel(app) {

    private val shareWorkerName = "share_${extra.accountInstance.signedInAccount.account.id}_${extra.url.md5}_${Clock.System.now().toEpochMilliseconds()}"
    private val saveWorkerName = "save_${extra.accountInstance.signedInAccount.account.id}_${extra.url.md5}_${Clock.System.now().toEpochMilliseconds()}"

    private val workManager = WorkManager.getInstance(getApplication())

    private val downloadWorkInputData: Data
        get() = workDataOf(
            DownloadMediaWorker.INPUT_URL to extra.url,
            DownloadMediaWorker.INPUT_ACCOUNT_ID to extra.accountInstance.signedInAccount.account.id,
            DownloadMediaWorker.INPUT_FILE_NAME to extra.filename,
            DownloadMediaWorker.INPUT_TAG to shareWorkerName
        )

    val saveWorkerLiveData = workManager.getWorkInfosForUniqueWorkLiveData(saveWorkerName)

    init {
        workManager.cancelUniqueWork(saveWorkerName)
        workManager.cancelUniqueWork(shareWorkerName)
    }

    fun enqueueShareWork() {
        workManager.beginUniqueWork(
            shareWorkerName,
            ExistingWorkPolicy.REPLACE,
            listOf(
                OneTimeWorkRequest.Builder(DownloadMediaWorker::class.java)
                    .setInputData(downloadWorkInputData)
                    .build(),
                OneTimeWorkRequest.from(ShareMediaWorker::class.java)
            )
        ).enqueue()
    }

    fun enqueueSaveWork() {
        workManager.beginUniqueWork(
            saveWorkerName,
            ExistingWorkPolicy.REPLACE,
            listOf(
                OneTimeWorkRequest.Builder(DownloadMediaWorker::class.java)
                    .setInputData(downloadWorkInputData)
                    .build(),
                OneTimeWorkRequest.from(SaveMediaWorker::class.java)
            )
        ).enqueue()
    }

    override fun onCleared() {
        super.onCleared()
        workManager.cancelUniqueWork(saveWorkerName)
        workManager.cancelUniqueWork(shareWorkerName)
    }

    companion object {

        private object MediaViewModelExtraKeyImpl : CreationExtras.Key<MediaViewModelExtra>

        val MEDIA_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<MediaViewModelExtra> =
            MediaViewModelExtraKeyImpl

    }

}