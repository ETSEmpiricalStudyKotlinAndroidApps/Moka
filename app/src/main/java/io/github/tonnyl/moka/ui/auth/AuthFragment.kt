package io.github.tonnyl.moka.ui.auth

import android.accounts.AccountManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.databinding.FragmentAuthBinding
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.util.insertNewAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AuthFragment : Fragment() {

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var binding: FragmentAuthBinding

    private val accountManager by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(requireContext())
    }

    private val args: AuthFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.authGetStarted.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    """
                    |${RetrofitClient.GITHUB_AUTHORIZE_URL}
                    |?client_id=${BuildConfig.CLIENT_ID}
                    |&redirect_uri=${RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI}
                    |&scope=${RetrofitClient.SCOPE}
                    |&state=${System.currentTimeMillis()}
                """.trimMargin()
                )
            }
            startActivity(intent)
        }

        viewModel.authTokenAndUserResult.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    resource.data?.let {
                        val (token, authUser) = it
                        initTokensAndGoToMainPage(
                            token,
                            authUser
                        )
                    }

                    binding.loadingAnimationView.visibility = View.GONE
                    binding.loadingAnimationView.cancelAnimation()

                    binding.authGetStarted.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    binding.loadingAnimationView.visibility = View.GONE
                    binding.loadingAnimationView.cancelAnimation()

                    binding.authGetStarted.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    binding.loadingAnimationView.visibility = View.VISIBLE
                    binding.loadingAnimationView.playAnimation()

                    binding.authGetStarted.visibility = View.GONE
                }
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

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