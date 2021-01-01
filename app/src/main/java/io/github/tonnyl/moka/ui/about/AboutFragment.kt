package io.github.tonnyl.moka.ui.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.setContent
import androidx.fragment.app.Fragment
import io.github.tonnyl.moka.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_container, container, false)
        (view as ViewGroup).setContent {
            AboutScreen()
        }
        return view
    }

}