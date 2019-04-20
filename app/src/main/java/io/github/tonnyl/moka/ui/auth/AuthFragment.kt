package io.github.tonnyl.moka.ui.auth

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory()).get(AuthViewModel::class.java)
    }

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProviders.of(requireActivity(), ViewModelFactory()).get(MainViewModel::class.java)
    }

    private lateinit var accountManager: AccountManager

    private val args: AuthFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountManager = AccountManager.get(requireContext())

        val accounts = accountManager.getAccountsByType(Authenticator.KEY_ACCOUNT_TYPE)

        if (accounts.isNotEmpty()) {
            accountManager.getAuthToken(accounts[0], Authenticator.KEY_AUTH_TYPE, null, true, { future ->
                if (future.isDone) {
                    val token = future.result.get(AccountManager.KEY_AUTHTOKEN).toString()

                    initTokensAndGoToMainPage(token, accounts[0].name, false)
                }
            }, null)
        }

        auth_get_started.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("""
                    |${RetrofitClient.GITHUB_AUTHORIZE_URL}
                    |?client_id=${BuildConfig.CLIENT_ID}
                    |&redirect_uri=${RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI}
                    |&scope=${RetrofitClient.SCOPE}
                    |&state=${System.currentTimeMillis()}
                """.trimMargin())
            }
            startActivity(intent)
        }

        viewModel.accessTokenResult.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let {
                        initTokensAndGoToMainPage(it.first, it.second.login, true)
                    }

                    loading_animation_view.visibility = View.GONE
                    loading_animation_view.cancelAnimation()

                    auth_get_started.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    loading_animation_view.visibility = View.GONE
                    loading_animation_view.cancelAnimation()

                    auth_get_started.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    loading_animation_view.visibility = View.VISIBLE
                    loading_animation_view.playAnimation()

                    auth_get_started.visibility = View.GONE
                }
            }
        })

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val codeArg = args.code
        val stateArg = args.state

        if (!codeArg.isNullOrEmpty() && !stateArg.isNullOrEmpty()) {
            viewModel.getAccessToken(codeArg, stateArg)
        }
    }

    private fun initTokensAndGoToMainPage(token: String, login: String, shouldAddNew: Boolean) {
        val account = Account(login, Authenticator.KEY_ACCOUNT_TYPE)

        if (shouldAddNew) {
            accountManager.addAccountExplicitly(account, "", Bundle().apply {
                putString(Authenticator.KEY_LOGIN, login)
            })
            accountManager.setAuthToken(account, Authenticator.KEY_AUTH_TYPE, token)
        }

        NetworkClient.accessToken = token
        RetrofitClient.lastToken = token

        mainViewModel.login.value = login

        findNavController().navigate(R.id.action_to_main)
    }

}