package io.github.tonnyl.moka.ui.media

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.work.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import io.github.tonnyl.moka.util.md5
import io.github.tonnyl.moka.work.DownloadMediaWorker
import io.github.tonnyl.moka.work.SaveMediaWorker
import io.github.tonnyl.moka.work.ShareMediaWorker
import io.tonnyl.moka.common.AccountInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class MediaViewModelExtra(
    val accountInstance: AccountInstance,
    val url: String,
    val filename: String,
    val mediaType: MediaType
)

@ExperimentalPagingApi
@ExperimentalSerializationApi
class MediaViewModel(
    app: Application,
    private val extra: MediaViewModelExtra
) : AndroidViewModel(app) {

    private val shareWorkerName = "share_${extra.accountInstance.signedInAccount.account.id}_${extra.url.md5}_${
        Clock.System.now().toEpochMilliseconds()
    }"
    private val saveWorkerName = "save_${extra.accountInstance.signedInAccount.account.id}_${extra.url.md5}_${
        Clock.System.now().toEpochMilliseconds()
    }"

    private val workManager = WorkManager.getInstance(getApplication())

    private val downloadWorkInputData: Data
        get() = workDataOf(
            DownloadMediaWorker.INPUT_URL to extra.url,
            DownloadMediaWorker.INPUT_ACCOUNT_ID to extra.accountInstance.signedInAccount.account.id,
            DownloadMediaWorker.INPUT_FILE_NAME to extra.filename,
            DownloadMediaWorker.INPUT_TAG to shareWorkerName
        )

    val saveWorkerInfo = workManager.getWorkInfosForUniqueWorkLiveData(saveWorkerName)
    val shareWorkerInfo = workManager.getWorkInfosForUniqueWorkLiveData(shareWorkerName)

    val mediaSource = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(getApplication()))
        .createMediaSource(MediaItem.fromUri(extra.url))

    private val _playerProgress = MutableLiveData(0L)
    val playerProgress: LiveData<Long>
        get() = _playerProgress

    private var playProgressJob: Job? = null

    private val _playState = MutableLiveData(false)
    val playState: LiveData<Boolean>
        get() = _playState

    private val _isReady = MutableLiveData(false)
    val isReady: LiveData<Boolean>
        get() = _isReady

    init {
        workManager.cancelUniqueWork(saveWorkerName)
        workManager.cancelUniqueWork(shareWorkerName)
    }

    fun enqueueShareWork() {
        workManager.beginUniqueWork(
            shareWorkerName,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(DownloadMediaWorker::class.java)
                .setInputData(downloadWorkInputData)
                .build()
        ).then(
            OneTimeWorkRequest.from(ShareMediaWorker::class.java)
        ).enqueue()
    }

    fun enqueueSaveWork() {
        workManager.beginUniqueWork(
            saveWorkerName,
            ExistingWorkPolicy.KEEP,
            OneTimeWorkRequest.Builder(DownloadMediaWorker::class.java)
                .setInputData(downloadWorkInputData)
                .build(),
        ).then(
            OneTimeWorkRequest.from(SaveMediaWorker::class.java)
        ).enqueue()
    }

    fun playingChanged(
        isPlaying: Boolean,
        player: Player?
    ) {
        _playState.value = isPlaying

        playProgressJob?.cancel()

        if (isPlaying) {
            playProgressJob = viewModelScope.launch {
                while (true) {
                    delay(1_000)
                    _playerProgress.value = 0L.coerceAtLeast((player?.currentPosition) ?: 0) / 1000
                }
            }
        }
    }

    fun adjustProgress(position: Long) {
        playProgressJob?.cancel()

        _playerProgress.value = position
    }

    fun updateIsReadyState(ready: Boolean) {
        _isReady.value = ready
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