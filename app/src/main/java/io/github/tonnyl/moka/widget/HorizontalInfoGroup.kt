package io.github.tonnyl.moka.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import io.github.tonnyl.moka.R

class HorizontalInfoGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    private val labelText: AppCompatTextView
    private val infoText: AppCompatTextView

    private var label: String?
    private var info: CharSequence? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_horizontal_info, this, true)
        labelText = view.findViewById(R.id.label)
        infoText = view.findViewById(R.id.content)

        context.obtainStyledAttributes(attrs, R.styleable.HorizontalInfoGroup).run {
            label = getString(R.styleable.HorizontalInfoGroup_label)

            recycle()
        }

        labelText.text = label

        orientation = HORIZONTAL
    }

    fun setInfoText(text: CharSequence?) {
        info = text
        infoText.text = info
    }

}