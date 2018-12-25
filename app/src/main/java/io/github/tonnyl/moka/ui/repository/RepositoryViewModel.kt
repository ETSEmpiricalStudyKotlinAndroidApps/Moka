package io.github.tonnyl.moka.ui.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.RepositoryGraphQL
import io.github.tonnyl.moka.net.Resource

class RepositoryViewModel(
        private val login: String,
        private val repositoryName: String
) : ViewModel() {

    val repositoryResult: LiveData<Resource<RepositoryGraphQL>> = Transformations.map(RepositoryLiveData(login, repositoryName)) { it }

    fun setBranchName(branchName: String): LiveData<Resource<Pair<String, String>?>> = Transformations.map(RepositoryReadmeFileNameLiveData(login, repositoryName, branchName)) { it }

    fun setExpression(expression: String): LiveData<Resource<String>> = Transformations.map(RepositoryReadmeFileLiveData(login, repositoryName, expression)) { it }

}