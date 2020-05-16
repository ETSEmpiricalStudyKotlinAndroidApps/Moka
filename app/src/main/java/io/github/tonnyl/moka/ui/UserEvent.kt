package io.github.tonnyl.moka.ui

import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.type.ReactionContent

sealed class UserEvent {

    object ShowSearch : UserEvent()

    object ShowAccounts : UserEvent()

    class ShowReactionDialog(
        val reactableId: String,
        val userHasReactedContents: Array<String>?
    ) : UserEvent()

    object DismissReactionDialog : UserEvent()

    class React(
        val resource: Resource<Unit>,
        val content: ReactionContent,
        val reactableId: String,
        val isSelected: Boolean
    ) : UserEvent()

}