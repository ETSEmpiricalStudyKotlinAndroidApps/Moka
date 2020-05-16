package io.github.tonnyl.moka.ui.reaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.Event
import io.github.tonnyl.moka.ui.profile.ProfileEvent

class AddReactionViewModel(
    val args: AddReactionDialogFragmentArgs
) : ViewModel() {

    private val _event = MutableLiveData<Event<ProfileEvent>>()
    val event: LiveData<Event<ProfileEvent>>
        get() = _event

    private val viewerHasReactedContents by lazy(LazyThreadSafetyMode.NONE) {
        args.viewerHasReactedContents?.map {
            ReactionContent.safeValueOf(it)
        }?.toSet() ?: emptySet()
    }

    fun isContentSelected(content: ReactionContent): Boolean =
        viewerHasReactedContents.contains(content)

}