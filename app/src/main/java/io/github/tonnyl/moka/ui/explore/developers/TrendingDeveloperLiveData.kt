package io.github.tonnyl.moka.ui.explore.developers

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.TrendingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class TrendingDeveloperLiveData : LiveData<Resource<List<TrendingDeveloper>>>() {

    private var call: Call<List<TrendingDeveloper>>? = null

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
                .listTrendingDevelopers(language = "all", since = "daily")

        call?.enqueue(object : Callback<List<TrendingDeveloper>> {

            override fun onFailure(call: Call<List<TrendingDeveloper>>, t: Throwable) {
                Timber.e(t)

                postValue(Resource.error(t.message, null))
            }

            override fun onResponse(call: Call<List<TrendingDeveloper>>, response: Response<List<TrendingDeveloper>>) {
                postValue(Resource.success(response.body()))
            }

        })
    }

}