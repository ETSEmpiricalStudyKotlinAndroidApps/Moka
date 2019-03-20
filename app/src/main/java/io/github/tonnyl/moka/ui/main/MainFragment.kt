package io.github.tonnyl.moka.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.github.tonnyl.moka.R
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add a default argument to nav controller.
        val host: NavHostFragment = childFragmentManager.findFragmentById(R.id.main_fragment_nav_host) as NavHostFragment
        nav_view.setupWithNavController(host.navController)

        AppBarConfiguration(host.navController.graph, drawer_layout)
    }

}