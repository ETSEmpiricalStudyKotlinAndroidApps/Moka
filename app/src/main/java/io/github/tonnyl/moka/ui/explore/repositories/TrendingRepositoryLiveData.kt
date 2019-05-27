package io.github.tonnyl.moka.ui.explore.repositories

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.TrendingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TrendingRepositoryLiveData : LiveData<Resource<List<TrendingRepository>>>() {

    private var call: Call<List<TrendingRepository>>? = null

    init {
        refresh()
    }

    override fun onInactive() {
        super.onInactive()
        if (call?.isCanceled == false) {
            call?.cancel()
        }
    }

    @MainThread
    fun refresh() {
        value = Resource.loading(null)

        call = RetrofitClient.createService(TrendingService::class.java)
                .listTrendingRepositories(language = "all", since = "daily")

        call?.enqueue(object : Callback<List<TrendingRepository>> {

            override fun onFailure(call: Call<List<TrendingRepository>>, t: Throwable) {
                Timber.e(t)

                postValue(Resource.error(t.message, null))
            }

            override fun onResponse(call: Call<List<TrendingRepository>>, response: Response<List<TrendingRepository>>) {
                postValue(Resource.success(response.body()))
            }

        })
    }

}