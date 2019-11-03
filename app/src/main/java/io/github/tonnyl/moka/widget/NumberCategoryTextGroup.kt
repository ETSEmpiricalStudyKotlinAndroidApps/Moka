package io.github.tonnyl.moka.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import io.github.tonnyl.moka.R

class NumberCategoryTextGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val numberText: AppCompatTextView
    private val categoryText: AppCompatTextView

    private var number: String? = null
    private var category: String?

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_number_category, this, true)
        numberText = view.findViewById(R.id.number)
        categoryText = view.findViewById(R.id.caption)

        context.obtainStyledAttributes(attrs, R.styleable.NumberCategoryTextGroup).run {
            category = getString(R.styleable.NumberCategoryTextGroup_category)

            recycle()
        }

        categoryText.text = category

        orientation = VERTICAL
    }

    fun setNumberText(text: String?) {
        number = text
        numberText.text = number
    }

}