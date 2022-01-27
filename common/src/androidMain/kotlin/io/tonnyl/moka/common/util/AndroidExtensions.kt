package io.tonnyl.moka.common.util

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

fun CreationExtras.getApplication(): Application {
    return checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
}

fun <T> CreationExtras.getExtra(key: CreationExtras.Key<T>): T {
    return checkNotNull(this[key])
}