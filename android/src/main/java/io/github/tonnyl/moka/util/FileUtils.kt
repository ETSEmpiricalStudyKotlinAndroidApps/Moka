package io.github.tonnyl.moka.util

import android.net.Uri
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
    private val DOWNLOAD_DIRECTLY_FILE_EXTENSIONS = listOf(
        ".zip", ".7z", ".rar", ".tar.gz", ".tgz", ".tar.Z", ".tar.bz2", ".tbz2", ".tar.lzma", ".tlz", ".apk", ".jar",
        ".dmg", ".pdf", ".ico", ".docx", ".doc", ".xlsx", ".hwp", ".pptx", ".show", ".mp3", ".ogg", ".ipynb", ".exe"
    )

    const val ROOT_FILE_DIR_NAME = "Moka"

    fun isSupportedImage(filename: String): Boolean = isXFile(
        filename = filename,
        extensions = SUPPORTED_IMAGE_EXTENSIONS
    )

    fun isSupportedVideo(filename: String): Boolean = isXFile(
        filename = filename,
        extensions = SUPPORTED_VIDEO_EXTENSIONS
    )

    fun isDownloadDirectlyFile(filename: String): Boolean = isXFile(
        filename = filename,
        extensions = DOWNLOAD_DIRECTLY_FILE_EXTENSIONS
    )

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

    private fun getFileExtension(filename: String): String? {
        val encodedFilename = Uri.encode(filename)
        if (encodedFilename.isEmpty()
            || encodedFilename.isBlank()
        ) {
            return null
        }

        return MimeTypeMap.getFileExtensionFromUrl(encodedFilename)
    }

    private fun isXFile(
        filename: String,
        extensions: List<String>
    ): Boolean {
        val ext = getFileExtension(filename)
        if (ext.isNullOrEmpty()) {
            return false
        }

        return extensions.find { it.endsWith(ext) } != null
    }

}