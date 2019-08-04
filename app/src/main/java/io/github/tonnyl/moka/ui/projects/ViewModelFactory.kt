package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.tonnyl.moka.db.dao.ProjectsDao

class ViewModelFactory(
    private val isMyself: Boolean,
    private val projectsDao: ProjectsDao,
    private val repositoryName: String?
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProjectsViewModel(isMyself, projectsDao, repositoryName) as T
    }

}