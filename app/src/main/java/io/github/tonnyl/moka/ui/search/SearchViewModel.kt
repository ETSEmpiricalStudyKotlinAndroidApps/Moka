package io.github.tonnyl.moka.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {

    val input = MutableLiveData<String>()

}