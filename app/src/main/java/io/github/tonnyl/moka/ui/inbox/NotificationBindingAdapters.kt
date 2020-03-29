package io.github.tonnyl.moka.ui.inbox

import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

@BindingAdapter("unread")
fun AppCompatTextView.isUnreadNotification(unread: Boolean?) {
    setTypeface(
        typeface, if (unread == true) {
            Typeface.BOLD
        } else {
            Typeface.NORMAL
        }
    )
}