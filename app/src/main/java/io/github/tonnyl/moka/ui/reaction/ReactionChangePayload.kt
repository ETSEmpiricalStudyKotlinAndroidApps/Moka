package io.github.tonnyl.moka.ui.reaction

data class ReactionChangePayload(
    val index: Int,
    val change: ReactionChange?
)