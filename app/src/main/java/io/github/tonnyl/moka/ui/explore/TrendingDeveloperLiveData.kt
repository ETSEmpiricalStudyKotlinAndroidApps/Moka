package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.TrendingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TrendingDeveloperLiveData : LiveData<Response<List<TrendingDeveloper>>>() {

    private val call = RetrofitClient.createService(TrendingService::class.java, null)
            .listTrendingDevelopers(language = "all", since = "daily")

    init {
        call.enqueue(object : Callback<List<TrendingDeveloper>> {

            override fun onFailure(call: Call<List<TrendingDeveloper>>, t: Throwable) {
                Timber.e(t, "listTrendingDevelopers error: ${t.message}")
            }

            override fun onResponse(call: Call<List<TrendingDeveloper>>, response: Response<List<TrendingDeveloper>>) {
                value = response
            }

        })
    }

    override fun onInactive() {
        super.onInactive()
        if (!call.isCanceled) {
            call.cancel()
        }
    }

}