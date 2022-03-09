package io.github.tonnyl.moka.work

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.tonnyl.moka.util.shareMedia

class ShareMediaWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val fileUri = inputData.getString(DownloadMediaWorker.OUTPUT_FILE_URI)?.let {
            Uri.parse(it)
        } ?: return Result.failure()
       val mimeType = inputData.getString(DownloadMediaWorker.OUTPUT_FILE_MIME_TYPE) ?: return Result.failure()

        applicationContext.shareMedia(
            uri = fileUri,
            mimeType = mimeType,
        )

        return Result.success()
    }

}