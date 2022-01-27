package io.tonnyl.moka.common.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.ExperimentalPagingApi
import io.tonnyl.moka.common.ui.timeline.TimelineViewModel
import io.tonnyl.moka.common.util.getExtra
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
class ViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            TimelineViewModel::class.java -> {
                TimelineViewModel(extra = extras.getExtra(key = TimelineViewModel.TIMELINE_VIEW_MODEL_EXTRA_KEY))
            }
            else -> {
                throw IllegalArgumentException("Unknown class $modelClass")
            }
        } as T
    }

}