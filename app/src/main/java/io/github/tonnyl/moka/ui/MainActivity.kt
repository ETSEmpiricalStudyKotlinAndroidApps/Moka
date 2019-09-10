package io.github.tonnyl.moka.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.ui.auth.AuthActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (application as MokaApp).loginAccounts.observe(this, Observer {
            if (it.firstOrNull() == null) {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            } else {
                val (_, token, user) = it.first()
                GraphQLClient.accessToken.set(token)
                RetrofitClient.accessToken.set(token)

                viewModel.currentUser.value = user
            }
        })

        viewModel.currentUser.observe(this, Observer {
            viewModel.getUserProfile()
        })
    }

}
