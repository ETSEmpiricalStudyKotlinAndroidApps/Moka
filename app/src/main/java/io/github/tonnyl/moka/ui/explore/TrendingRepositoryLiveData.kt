package io.github.tonnyl.moka.ui.explore

import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.TrendingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TrendingRepositoryLiveData : LiveData<Response<List<TrendingRepository>>>() {

    private val call = RetrofitClient.createService(TrendingService::class.java, null)
            .listTrendingRepositories(language = "all", since = "daily")

    init {
        call.enqueue(object : Callback<List<TrendingRepository>> {

            override fun onFailure(call: Call<List<TrendingRepository>>, t: Throwable) {
                Timber.e(t, "listTrendingRepositories error: ${t.message}")
            }

            override fun onResponse(call: Call<List<TrendingRepository>>, response: Response<List<TrendingRepository>>) {
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