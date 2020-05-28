package io.github.tonnyl.moka.util

import android.os.Build
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.drawerlayout.widget.DrawerLayout

fun DrawerLayout.shouldCloseDrawerFromBackPress(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // If we're running on Q, and this call to closeDrawers is from a key event
        // (for back handling), we should only honor it IF the device is not currently
        // in gesture mode. We approximate that by checking the system gesture insets
        return rootWindowInsets?.let {
            val systemGestureInsets = it.systemGestureInsets
            return systemGestureInsets.left == 0 && systemGestureInsets.right == 0
        } ?: false
    }
    // On P and earlier, always close the drawer
    return true
}

fun View.showOverflowMenu(@MenuRes menuId: Int, listener: PopupMenu.OnMenuItemClickListener) {
    val popup = PopupMenu(context, this)
    popup.setOnMenuItemClickListener(listener)
    popup.menuInflater.inflate(menuId, popup.menu)
    popup.show()
}