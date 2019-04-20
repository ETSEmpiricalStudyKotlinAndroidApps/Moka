package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.CurrentLevelTreeViewQuery
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import timber.log.Timber

class RepositoryReadmeFileNameLiveData(
        private val login: String,
        private val name: String,
        private val branchName: String
) : LiveData<Resource<Pair<String, String>>>() {

    private val call = NetworkClient.apolloClient
            .query(CurrentLevelTreeViewQuery.builder()
                    .login(login)
                    .repoName(name)
                    .expression("$branchName:")
                    .build())

    override fun onInactive() {
        super.onInactive()

        if (!call.isCanceled) {
            call.cancel()
        }
    }

    fun refresh() {
        value = Resource.loading(null)

        call.enqueue(object : ApolloCall.Callback<CurrentLevelTreeViewQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                postValue(Resource.error(e.message, null))
            }

            override fun onResponse(response: Response<CurrentLevelTreeViewQuery.Data>) {
                val readmeFiles = response.data()
                        ?.repository()
                        ?.`object`()
                        ?.fragments()
                        ?.treeAbstract()
                        ?.entries()
                        ?.filter {
                            it.name().toLowerCase().contains("readme")
                        }

                if (readmeFiles.isNullOrEmpty()) {
                    Resource(Status.SUCCESS, null, null)
                } else {
                    val mdIndex = readmeFiles.indexOfFirst { it.name().endsWith(".md") }
                    postValue(if (mdIndex >= 0) {
                        Resource.success(Pair("md", readmeFiles[mdIndex].name()))
                    } else {
                        val htmlIndex = readmeFiles.indexOfFirst { it.name().endsWith(".html") }
                        if (htmlIndex >= 0) {
                            Resource.success(Pair("html", readmeFiles[htmlIndex].name()))
                        } else {
                            val plainIndex = readmeFiles.indexOfFirst { it.name().toLowerCase() == "readme" }
                            if (plainIndex >= 0) {
                                Resource.success(Pair("plain", readmeFiles[plainIndex].name()))
                            } else {
                                Resource.success(null)
                            }
                        }
                    })
                }
            }

        })
    }

}