package io.github.tonnyl.moka.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.ui.auth.AuthFragmentArgs

class MainActivity : AppCompatActivity() {

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
    }

}
