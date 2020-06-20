package io.github.tonnyl.moka.ui.profile

import io.github.tonnyl.moka.data.Gist2
import io.github.tonnyl.moka.data.RepositoryItem

sealed class ProfileEvent {

    object ViewRepositories : ProfileEvent()

    object ViewStars : ProfileEvent()

    object ViewFollowers : ProfileEvent()

    object ViewFollowings : ProfileEvent()

    object ViewProjects : ProfileEvent()

    object EditStatus : ProfileEvent()

    class ViewRepository(val repository: RepositoryItem) : ProfileEvent()

    class ViewGist(val gist: Gist2) : ProfileEvent()

    class PinnedItemUpdate(val index: Int) : ProfileEvent()

}