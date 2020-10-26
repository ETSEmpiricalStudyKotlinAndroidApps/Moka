package io.github.tonnyl.moka.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.ActivityMainBinding
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.ui.UserEvent.*
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.reaction.AddReactionDialogFragmentArgs
import io.github.tonnyl.moka.util.HeightTopWindowInsetsListener
import io.github.tonnyl.moka.util.NoopWindowInsetsListener
import io.github.tonnyl.moka.util.updateForTheme

class MainActivity : AppCompatActivity(), NavigationHost {

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(applicationContext as MokaApp)
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateForTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.contentContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        binding.contentContainer.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)

        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)

        // add a default argument to nav controller.
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_fragment_nav_host) as NavHostFragment
        navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)

        AppBarConfiguration(
            TOP_LEVEL_DESTINATIONS,
            binding.drawerLayout,
            fallbackOnNavigateUpListener = {
                true
            })

        (application as MokaApp).loginAccounts.observe(this) {
            if (it.firstOrNull() == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                val (_, token, user) = it.first()
                GraphQLClient.accessToken.set(token)
                RetrofitClient.accessToken.set(token)

                viewModel.currentUser.value = user
            }
        }

        viewModel.currentUser.observe(this) {
            viewModel.getUserProfile()
        }

        viewModel.event.observe(this, EventObserver {
            when (it) {
                is ShowSearch -> {
                    navController.navigate(R.id.search_fragment)
                }
                is ShowAccounts -> {
                    navController.navigate(R.id.account_dialog)
                }
                is ShowReactionDialog -> {
                    navController.navigate(
                        R.id.add_reaction_dialog,
                        AddReactionDialogFragmentArgs(
                            it.userHasReactedContents,
                            it.reactableId
                        ).toBundle()
                    )
                }
                is DismissReactionDialog -> {
                    navController.navigateUp()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean = navController.navigateUp()

    companion object {

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.nav_timeline,
            R.id.nav_explore,
            R.id.nav_inbox,
            R.id.nav_projects,
            R.id.nav_settings,
            R.id.nav_about,
            R.id.nav_help_faq,
            R.id.nav_feedback
        )

    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, binding.drawerLayout)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}
