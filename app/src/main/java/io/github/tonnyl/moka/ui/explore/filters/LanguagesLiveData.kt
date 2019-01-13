package io.github.tonnyl.moka.ui.explore.filters

import androidx.lifecycle.LiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset

class LanguagesLiveData(private val inputStream: InputStream) : LiveData<List<LocalLanguage>>() {

    private val disposable: Disposable = Observable.fromCallable {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer, Charset.forName("UTF-8"))
        Gson().fromJson<List<LocalLanguage>>(json, object : TypeToken<List<LocalLanguage>>() {}.type)
    }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                value = it
            }, {
                Timber.e(it, "read json file error: ${it.message}")
            })

    override fun onInactive() {
        super.onInactive()
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

}