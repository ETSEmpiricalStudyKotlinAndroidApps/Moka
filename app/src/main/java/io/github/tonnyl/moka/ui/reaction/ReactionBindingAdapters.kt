package io.github.tonnyl.moka.ui.reaction

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.type.ReactionContent.*

@BindingAdapter(
    "reactionGroups",
    "viewerCanReact",
    requireAll = true
)
fun RecyclerView.reactionGroups(
    reactions: List<ReactionGroup>?,
    viewerCanReact: Boolean
) {
    if (reactions.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE

        adapter = ((adapter as? ReactionGroupAdapter)
            ?: ReactionGroupAdapter(viewerCanReact)).apply {
            submitList(reactions)
        }
    }
}

@BindingAdapter(
    "reactionContent",
    "reactedUsersTotalCount",
    requireAll = true
)
fun Chip.reactionGroup(
    reactionContent: ReactionContent?,
    reactedUsersTotalCount: Int?
) {
    reactionContent ?: return
    reactedUsersTotalCount ?: return

    val content = resources.getString(
        when (reactionContent) {
            THUMBS_UP -> {
                R.string.emoji_thumbs_up
            }
            THUMBS_DOWN -> {
                R.string.emoji_thumbs_down
            }
            LAUGH -> {
                R.string.emoji_laugh
            }
            HOORAY -> {
                R.string.emoji_hooray
            }
            CONFUSED -> {
                R.string.emoji_confused
            }
            HEART -> {
                R.string.emoji_heart
            }
            ROCKET -> {
                R.string.emoji_rocket
            }
            EYES -> {
                R.string.emoji_eyes
            }
            UNKNOWN__ -> { // 🤔🤔🤔
                R.string.emoji_unknown
            }
        }
    )

    text = context.getString(R.string.reaction_group_and_count, content, reactedUsersTotalCount)
}