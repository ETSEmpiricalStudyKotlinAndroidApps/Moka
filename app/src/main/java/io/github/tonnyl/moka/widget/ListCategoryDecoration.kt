package io.github.tonnyl.moka.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R

class ListCategoryDecoration(
    parent: RecyclerView,
    private val text: String
) : RecyclerView.ItemDecoration() {

    private val layout: View = LayoutInflater.from(parent.context)
        .inflate(R.layout.layout_list_category_header, parent, false)

    init {
        layout.findViewById<AppCompatTextView>(R.id.list_category_text).text = text

        layout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        parent.adapter?.let {
            if (it.itemCount > 0) {
                layout.layout(0, 0, layout.measuredWidth, layout.measuredHeight)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.childCount > 0) {
            val child = parent.getChildAt(0)
            if (parent.getChildAdapterPosition(child) != 0) {
                return
            }
            c.translate(0f, (child.top - layout.height).toFloat())
        }
        layout.draw(c)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.set(0, layout.measuredHeight, 0, 0)
        }
    }

}