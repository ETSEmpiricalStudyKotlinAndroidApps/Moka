package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.data.Resource

class RepositoriesViewModel(
        private val login: String,
        private val repositoryType: String
) : ViewModel() {

    val repositoriesResults: LiveData<Resource<List<RepositoryAbstract>>> = Transformations.map(RepositoriesLiveData(login, repositoryType)) { it }

}