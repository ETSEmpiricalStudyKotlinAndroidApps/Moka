package io.github.tonnyl.moka.ui.profile

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class PinnedItemDecoration(private val space: Int) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            if (layoutManager.orientation == RecyclerView.HORIZONTAL) {
                outRect.left = if (parent.getChildAdapterPosition(view) == 0) {
                    space
                } else {
                    0
                }
                outRect.right = space
            }
        }
    }

}