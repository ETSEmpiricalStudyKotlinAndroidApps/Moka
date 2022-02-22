package io.github.tonnyl.moka.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.ui.auth.AuthViewModel
import io.github.tonnyl.moka.ui.branches.BranchesViewModel
import io.github.tonnyl.moka.ui.commit.CommitViewModel
import io.github.tonnyl.moka.ui.commits.CommitsViewModel
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.filters.ExploreFiltersViewModel
import io.github.tonnyl.moka.ui.file.FileViewModel
import io.github.tonnyl.moka.ui.inbox.InboxViewModel
import io.github.tonnyl.moka.ui.issue.IssueViewModel
import io.github.tonnyl.moka.ui.issues.IssuesViewModel
import io.github.tonnyl.moka.ui.issues.create.CreateIssueViewModel
import io.github.tonnyl.moka.ui.media.MediaViewModel
import io.github.tonnyl.moka.ui.pr.PullRequestViewModel
import io.github.tonnyl.moka.ui.pr.thread.CommentThreadViewModel
import io.github.tonnyl.moka.ui.profile.ProfileViewModel
import io.github.tonnyl.moka.ui.profile.edit.EditProfileViewModel
import io.github.tonnyl.moka.ui.profile.status.EditStatusViewModel
import io.github.tonnyl.moka.ui.prs.PullRequestsViewModel
import io.github.tonnyl.moka.ui.release.ReleaseViewModel
import io.github.tonnyl.moka.ui.release.assets.ReleaseAssetsViewModel
import io.github.tonnyl.moka.ui.releases.ReleasesViewModel
import io.github.tonnyl.moka.ui.repositories.RepositoriesViewModel
import io.github.tonnyl.moka.ui.repository.RepositoryViewModel
import io.github.tonnyl.moka.ui.repository.files.RepositoryFilesViewModel
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.settings.SettingsViewModel
import io.github.tonnyl.moka.ui.topics.RepositoryTopicsViewModel
import io.github.tonnyl.moka.ui.users.UsersViewModel
import io.tonnyl.moka.common.util.getApplication
import io.tonnyl.moka.common.util.getExtra
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            MainViewModel::class.java -> {
                MainViewModel(extras.getApplication())
            }
            AuthViewModel::class.java -> {
                AuthViewModel(extras.getApplication())
            }
            SearchViewModel::class.java -> {
                SearchViewModel(
                    app = extras.getApplication(),
                    extra = extras.getExtra(key = SearchViewModel.SEARCH_VIEW_MODEL_EXTRA_KEY)
                )
            }
            UsersViewModel::class.java -> {
                UsersViewModel(extra = extras.getExtra(key = UsersViewModel.USERS_VIEW_MODEL_EXTRA_KEY))
            }
            RepositoryTopicsViewModel::class.java -> {
                RepositoryTopicsViewModel(extra = extras.getExtra(key = RepositoryTopicsViewModel.REPOSITORY_TOPICS_VIEW_MODEL_EXTRA_KEY))
            }
            SettingsViewModel::class.java -> {
                SettingsViewModel(
                    app = extras.getApplication(),
                    extra = extras.getExtra(key = SettingsViewModel.SETTINGS_VIEW_MODEL_EXTRA_KEY)
                )
            }
            RepositoryViewModel::class.java -> {
                RepositoryViewModel(extra = extras.getExtra(key = RepositoryViewModel.REPOSITORY_VIEW_MODEL_EXTRA_KEY))
            }
            RepositoryFilesViewModel::class.java -> {
                RepositoryFilesViewModel(extra = extras.getExtra(key = RepositoryFilesViewModel.REPOSITORY_FILES_VIEW_MODEL_EXTRA_KEY))
            }
            RepositoriesViewModel::class.java -> {
                RepositoriesViewModel(extra = extras.getExtra(key = RepositoriesViewModel.REPOSITORIES_VIEW_MODEL_EXTRA_KEY))
            }
            FileViewModel::class.java -> {
                FileViewModel(
                    app = extras.getApplication(),
                    extra = extras.getExtra(key = FileViewModel.FILE_VIEW_MODEL_EXTRA_KEY)
                )
            }
            ReleasesViewModel::class.java -> {
                ReleasesViewModel(extra = extras.getExtra(key = ReleasesViewModel.RELEASES_VIEW_MODEL_EXTRA_KEY))
            }
            ReleaseViewModel::class.java -> {
                ReleaseViewModel(extra = extras.getExtra(key = ReleaseViewModel.RELEASE_VIEW_MODEL_EXTRA_KEY))
            }
            BranchesViewModel::class.java -> {
                BranchesViewModel(extra = extras.getExtra(key = BranchesViewModel.BRANCHES_VIEW_MODEL_EXTRA_KEY))
            }
            CommitViewModel::class.java -> {
                CommitViewModel(extra = extras.getExtra(key = CommitViewModel.COMMIT_VIEW_MODEL_EXTRA_KEY))
            }
            CommitsViewModel::class.java -> {
                CommitsViewModel(extra = extras.getExtra(key = CommitsViewModel.COMMITS_VIEW_MODEL_EXTRA_KEY))
            }
            ExploreViewModel::class.java -> {
                ExploreViewModel(extra = extras.getExtra(key = ExploreViewModel.EXPLORE_VIEW_MODEL_EXTRA_KEY))
            }
            InboxViewModel::class.java -> {
                InboxViewModel(
                    app = extras.getApplication(),
                    extra = extras.getExtra(key = InboxViewModel.INBOX_VIEW_MODEL_EXTRA_KEY)
                )
            }
            IssueViewModel::class.java -> {
                IssueViewModel(extra = extras.getExtra(key = IssueViewModel.ISSUE_VIEW_MODEL_EXTRA_KEY))
            }
            IssuesViewModel::class.java -> {
                IssuesViewModel(extra = extras.getExtra(key = IssuesViewModel.ISSUES_VIEW_MODEL_EXTRA_KEY))
            }
            PullRequestViewModel::class.java -> {
                PullRequestViewModel(extra = extras.getExtra(key = PullRequestViewModel.PULL_REQUEST_VIEW_MODEL_EXTRA_KEY))
            }
            CommentThreadViewModel::class.java -> {
                CommentThreadViewModel(extra = extras.getExtra(key = CommentThreadViewModel.COMMENT_THREAD_VIEW_MODEL_EXTRA_KEY))
            }
            ProfileViewModel::class.java -> {
                ProfileViewModel(extra = extras.getExtra(key = ProfileViewModel.PROFILE_VIEW_MODEL_EXTRA_KEY))
            }
            EditStatusViewModel::class.java -> {
                EditStatusViewModel(extra = extras.getExtra(key = EditStatusViewModel.EDIT_STATUS_VIEW_MODEL_EXTRA_KEY))
            }
            EditProfileViewModel::class.java -> {
                EditProfileViewModel(extra = extras.getExtra(key = EditProfileViewModel.EDIT_PROFILE_VIEW_MODEL_EXTRA_KEY))
            }
            PullRequestsViewModel::class.java -> {
                PullRequestsViewModel(extra = extras.getExtra(key = PullRequestsViewModel.PULL_REQUESTS_VIEW_MODEL_EXTRA_KEY))
            }
            ReleaseAssetsViewModel::class.java -> {
                ReleaseAssetsViewModel(extra = extras.getExtra(key = ReleaseAssetsViewModel.RELEASES_VIEW_MODEL_EXTRA_KEY))
            }
            MediaViewModel::class.java -> {
                MediaViewModel(
                    app = extras.getApplication(),
                    extra = extras.getExtra(key = MediaViewModel.MEDIA_VIEW_MODEL_EXTRA_KEY)
                )
            }
            ExploreFiltersViewModel::class.java -> {
                ExploreFiltersViewModel(extra = extras.getExtra(key = ExploreFiltersViewModel.FILTERS_VIEW_MODEL_EXTRA_KEY))
            }
            CreateIssueViewModel::class.java -> {
                CreateIssueViewModel(extra = extras.getExtra(key = CreateIssueViewModel.CREATE_ISSUE_VIEW_MODEL_EXTRA_KEY))
            }
            else -> {
                throw IllegalArgumentException("Unknown class $modelClass")
            }
        } as T
    }

}