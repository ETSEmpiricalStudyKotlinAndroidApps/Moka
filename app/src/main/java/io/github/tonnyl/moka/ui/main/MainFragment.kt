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
import io.github.tonnyl.moka.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // add a default argument to nav controller.
        val host: NavHostFragment = childFragmentManager.findFragmentById(R.id.main_fragment_nav_host) as NavHostFragment
        binding.navView.setupWithNavController(host.navController)

        AppBarConfiguration(host.navController.graph, binding.drawerLayout)
    }

}