package io.github.tonnyl.moka.util

import android.webkit.MimeTypeMap
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object FileUtils {

    private val EXTENSIONS = listOf(
        ".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp", ".bmp", ".heic", ".heif"
    )

    const val ROOT_FILE_DIR_NAME = "Moka"

    fun isImage(filename: String): Boolean {
        if (filename.isEmpty()
            || filename.isBlank()
        ) {
            return false
        }

        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(filename)
        if (fileExtension.isNullOrEmpty()) {
            return false
        }

        return EXTENSIONS.find { it.endsWith(fileExtension) } != null
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

}