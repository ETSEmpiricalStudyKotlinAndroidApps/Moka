package io.github.tonnyl.moka.ui.reaction

sealed class ReactionChange(val position: Int) {

    class ReactionUpdate(position: Int) : ReactionChange(position)

    class ReactionRemove(position: Int) : ReactionChange(position)

    class ReactionInsert(position: Int) : ReactionChange(position)

}