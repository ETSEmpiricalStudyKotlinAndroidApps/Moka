package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.OrgRepositoryCardInfoQuery
import io.github.tonnyl.moka.UserRepositoryCardInfoQuery
import io.github.tonnyl.moka.data.Event
import com.apollographql.apollo.api.Response as ApolloResponse
import retrofit2.Response as RetrofitResponse

class EventsViewModel : ViewModel() {

    val results: LiveData<RetrofitResponse<List<Event>>> = Transformations.map(EventsLiveData("tonnyl")) { it }

    fun userRepositoryCard(login: String, repositoryName: String): LiveData<ApolloResponse<UserRepositoryCardInfoQuery.Data>> = Transformations.map((UserRepositoryCardLiveData(login, repositoryName))) { it }

    fun orgRepositoryCard(login: String, repositoryName: String): LiveData<ApolloResponse<OrgRepositoryCardInfoQuery.Data>> = Transformations.map((OrgRepositoryCardLiveData(login, repositoryName))) { it }

}