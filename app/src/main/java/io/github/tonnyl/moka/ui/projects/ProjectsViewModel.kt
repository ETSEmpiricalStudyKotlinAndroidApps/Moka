package io.github.tonnyl.moka.ui.projects

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Resource

class ProjectsViewModel(
    private val isMyself: Boolean,
    private val args: ProjectsFragmentArgs,
    app: Application
) : AndroidViewModel(app) {

    var userId: Long = 0L
    var login: String = ""

    private val _initialLoadStatus = MutableLiveData<Resource<List<Project>>>()
    val initialLoadStatus: LiveData<Resource<List<Project>>>
        get() = _initialLoadStatus

    val projectsResult = liveData {
        emitSource(
            when (args.projectsType) {
                ProjectsType.UsersProjects -> {
                    Pager(
                        config = MokaApp.defaultPagingConfig,
                        remoteMediator = ProjectRemoteMediator(
                            args.login,
                            isMyself,
                            MokaDataBase.getInstance(getApplication(), userId),
                            _initialLoadStatus
                        ),
                        pagingSourceFactory = {
                            MokaDataBase.getInstance(getApplication(), userId).projectsDao()
                                .projectsByUpdatedAt()
                        }
                    )
                }
                ProjectsType.OrganizationsProjects -> {
                    Pager(
                        config = MokaApp.defaultPagingConfig,
                        pagingSourceFactory = {
                            OrganizationsProjectsDataSource(
                                args.login,
                                _initialLoadStatus
                            )
                        }
                    )
                }
                ProjectsType.RepositoriesProjects -> {
                    Pager(
                        config = MokaApp.defaultPagingConfig,
                        pagingSourceFactory = {
                            RepositoriesProjectsDataSource(
                                args.login,
                                args.repositoryName,
                                _initialLoadStatus
                            )
                        }
                    )
                }
            }.liveData
        )
    }.cachedIn(viewModelScope)

}