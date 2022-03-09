package io.github.tonnyl.moka.work

import android.content.Context
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.util.StorageManager
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import logcat.LogPriority
import logcat.logcat
import java.io.File

class DownloadMediaWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val url = inputData.getString(INPUT_URL) ?: return Result.failure()
        val filename = inputData.getString(INPUT_FILE_NAME) ?: return Result.failure()
        val tag = inputData.getString(INPUT_TAG) ?: return Result.failure()

        val accountInstanceId = inputData.getLong(INPUT_ACCOUNT_ID, -1)
        if (accountInstanceId == -1L) {
            return Result.failure()
        }

        val accounts = (applicationContext as MokaApp).accountInstancesLiveData.value
        val account = accounts?.find { it.signedInAccount.account.id == accountInstanceId }
        if (account == null) {
            logcat(tag = TAG, priority = LogPriority.INFO) {
                "no account, do nothing"
            }

            return Result.failure()
        }

        val storageManager = StorageManager(context = applicationContext)
        val file = File("${storageManager.mediaDir}/${tag}_${filename}")

        val getOutputData = {
            val fileUri = FileProvider.getUriForFile(
                applicationContext,
                "${BuildConfig.APPLICATION_ID}.fileProvider",
                file
            )
            val mimeType = applicationContext.contentResolver
                .getType(fileUri)

            workDataOf(
                OUTPUT_FILE_URI to fileUri.toString(),
                OUTPUT_FILE_MIME_TYPE to mimeType,
                INPUT_FILE_NAME to filename,
                Output_FILE_PATH to file.absolutePath
            )
        }

        if (file.exists()) {
            return Result.success(getOutputData())
        }

        account.authenticatedKtorClient.get<HttpStatement>(urlString = url).execute { response: HttpResponse ->
            val channel: ByteReadChannel = response.receive()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                }
            }
        }

        return Result.success(getOutputData())
    }

    companion object {

        const val TAG = "DownloadMediaWorker"

        const val INPUT_URL = "input_url"
        const val INPUT_ACCOUNT_ID = "input_account_id"
        const val INPUT_FILE_NAME = "input_file_name"
        const val INPUT_TAG = "input_tag"

        const val OUTPUT_FILE_URI = "output_file_uri"
        const val OUTPUT_FILE_MIME_TYPE = "out_file_mime_type"
        const val Output_FILE_PATH = "output_file_path"

    }

}