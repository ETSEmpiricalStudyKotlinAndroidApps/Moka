package io.github.tonnyl.moka.net

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

open class AppExecutors {

    val diskIO: Executor = Executors.newSingleThreadExecutor()

    val networkIO: Executor = Executors.newFixedThreadPool(5)

    val mainThread: Executor = MainThreadExecutor()

    private class MainThreadExecutor : Executor {

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

}