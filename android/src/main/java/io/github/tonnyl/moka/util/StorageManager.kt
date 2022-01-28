package io.github.tonnyl.moka.util

import android.content.Context
import java.io.File

class StorageManager(private val context: Context) {

    /**
     * for media files, e.g. images, videos
     */
    val mediaDir: String
        get() =  "${context.filesDir}/media".mkdirs()

    /**
     * @param dir directory target to clear
     */
    fun clear(dir: String) {
        val file = File(dir)
        if (!file.exists()) {
            return
        }
        file.listFiles()?.forEach {
            it.deleteRecursively()
        }
    }

}