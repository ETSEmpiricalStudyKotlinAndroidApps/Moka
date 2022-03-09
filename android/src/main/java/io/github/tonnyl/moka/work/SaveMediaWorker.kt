package io.github.tonnyl.moka.work

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.tonnyl.moka.util.FileUtils
import kotlinx.datetime.Clock
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.io.File
import java.io.FileInputStream

/**
 * Only available for saving images/videos.
 */
class SaveMediaWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @SuppressLint("InlinedApi")
    override suspend fun doWork(): Result {
        val filepath = inputData.getString(DownloadMediaWorker.Output_FILE_PATH) ?: return Result.failure()
        val mimeType = inputData.getString(DownloadMediaWorker.OUTPUT_FILE_MIME_TYPE) ?: return Result.failure()
        val filename = inputData.getString(DownloadMediaWorker.INPUT_FILE_NAME) ?: return Result.failure()

        val uriToInsert: Uri
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.DATE_ADDED, Clock.System.now().toEpochMilliseconds())
        }
        when {
            mimeType.startsWith("image") -> {
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

                uriToInsert = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val dirDest = File(Environment.DIRECTORY_PICTURES, FileUtils.ROOT_FILE_DIR_NAME)
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "${dirDest}${File.separator}")
            }
            mimeType.startsWith("video") -> {
                values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)

                uriToInsert = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

                val dirDest = File(Environment.DIRECTORY_MOVIES, FileUtils.ROOT_FILE_DIR_NAME)
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, "${dirDest}${File.separator}")
            }
            else -> {
                throw IllegalArgumentException("unknown mime type: $mimeType")
            }
        }

        val resolver = applicationContext.contentResolver
        try {
            resolver.insert(uriToInsert, values)?.let { uri ->
                resolver.openOutputStream(uri)?.let { os ->
                    FileInputStream(File(filepath)).use { fis ->
                        FileUtils.copyFile(fis, os)
                    }
                }
            }
        } catch (e: Exception) {
            logcat(priority = LogPriority.ERROR, tag = TAG) {
                "failed to save file: ${e.asLog()}"
            }
        }

        return Result.success()
    }

    companion object {

        private const val TAG = "SaveMediaWorker"

    }

}