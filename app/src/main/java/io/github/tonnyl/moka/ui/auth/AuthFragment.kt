package io.github.tonnyl.moka.ui.auth

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.Recomposer
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.ui.core.setContent
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.res.vectorResource
import androidx.ui.unit.dp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.insertNewAccount
import io.github.tonnyl.moka.util.isDarkModeOn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()

    private val accountManager by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(requireContext())
    }

    private val args: AuthFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_container, container, false)

        (view as ViewGroup).setContent(Recomposer.current()) {
            MokaTheme(darkTheme = resources.isDarkModeOn) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            title = { Text("") },
                            navigationIcon = {
                                IconButton(
                                    onClick = { activity?.finish() },
                                    icon = { Icon(vectorResource(R.drawable.ic_close_24)) }
                                )
                            },
                            elevation = 0.dp
                        )
                    },
                    bodyContent = {
                        AuthScreenContent(
                            authTokenAndUserResult = viewModel.authTokenAndUserResult
                        )
                    }
                )
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authTokenAndUserResult.observe(viewLifecycleOwner, Observer { resource ->
            if (resource.status == Status.SUCCESS) {
                resource.data?.let {
                    val (token, authUser) = it
                    initTokensAndGoToMainPage(
                        token,
                        authUser
                    )
                }
            }
        })

        val codeArg = args.code
        val stateArg = args.state

        if (!codeArg.isNullOrEmpty() && !stateArg.isNullOrEmpty()) {
            viewModel.getAccessToken(codeArg, stateArg)
        }
    }

    private fun initTokensAndGoToMainPage(
        token: String,
        authenticatedUser: AuthenticatedUser
    ) {
        lifecycleScope.launchWhenCreated {
            try {
                withContext(Dispatchers.IO) {
                    accountManager.insertNewAccount(token, authenticatedUser)
                }

                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)

                activity?.finish()
            } catch (e: Exception) {
                Timber.e(e, "insertNewAccount error")
            }
        }
    }

}