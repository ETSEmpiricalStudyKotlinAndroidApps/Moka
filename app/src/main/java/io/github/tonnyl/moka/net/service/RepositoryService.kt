package io.github.tonnyl.moka.net.service

import io.github.tonnyl.moka.data.Repository
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Url

interface RepositoryService {

    fun getRepositoryInfo(@Url url: String): Observable<Response<Repository>>

}