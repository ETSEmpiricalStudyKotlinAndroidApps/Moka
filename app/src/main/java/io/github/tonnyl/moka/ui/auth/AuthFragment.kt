package io.github.tonnyl.moka.ui.auth

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
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.Status
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory()).get(AuthViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_auth, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth_get_started.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("""
                    |${RetrofitClient.GITHUB_AUTHORIZE_URL}
                    |?client_id=${BuildConfig.CLIENT_ID}
                    |&redirect_uri=${RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI}
                    |&scope=${RetrofitClient.SCOPE}
                """.trimMargin())
            }
            startActivity(intent)
        }

        viewModel.accessTokenResult.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
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

        val args = AuthFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))
        val codeArg = args.code
        if (codeArg != null) {
            viewModel.getAccessToken(codeArg)
        }
    }

}