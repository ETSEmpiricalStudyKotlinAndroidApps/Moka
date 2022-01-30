package io.github.tonnyl.moka.util

import android.webkit.MimeTypeMap
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    private val SUPPORTED_IMAGE_EXTENSIONS = listOf(
        ".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp", ".bmp", ".heic", ".heif"
    )
    private val SUPPORTED_VIDEO_EXTENSIONS = listOf(
        ".mp4", ".m4a", ".m4s", ".webm", ".mkv"
    )

    const val ROOT_FILE_DIR_NAME = "Moka"

    fun isSupportedImage(filename: String): Boolean {
        val mimeType = getMimeType(filename)
        if (mimeType.isNullOrEmpty()) {
            return false
        }

        return SUPPORTED_IMAGE_EXTENSIONS.find { it.endsWith(mimeType) } != null
    }

    fun isSupportedVideo(filename: String): Boolean {
        val mimeType = getMimeType(filename)
        if (mimeType.isNullOrEmpty()) {
            return false
        }

        return SUPPORTED_VIDEO_EXTENSIONS.find { it.endsWith(mimeType) } != null
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: InputStream, out: OutputStream): Boolean {
        val buf = ByteArray(4096)
        var len: Int
        while (sourceFile.read(buf).also { len = it } > 0) {
            Thread.yield()
            out.write(buf, 0, len)
        }
        out.close()
        return true
    }

    private fun getMimeType(filename: String): String? {
        if (filename.isEmpty()
            || filename.isBlank()
        ) {
            return null
        }

        return MimeTypeMap.getFileExtensionFromUrl(filename)
    }

}