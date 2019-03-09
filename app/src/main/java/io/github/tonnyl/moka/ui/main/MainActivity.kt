package io.github.tonnyl.moka.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.ui.auth.AuthFragmentArgs

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = intent.data
        if (data != null
                && data.authority.isNotEmpty()
                && data.authority == RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI_HOST
                && data.scheme == RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI_SCHEMA) {

            val code = data.getQueryParameter("code")
            val argBuilder = AuthFragmentArgs.Builder(code)
            findNavController(R.id.main_activity_nav_host).navigate(R.id.action_to_auth, argBuilder.build().toBundle())
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(MainViewModel::class.java)

        viewModel.login.observe(this, Observer {
            viewModel.getUserProfile()
        })

        viewModel.login.value = "tonnyl"
    }

}
