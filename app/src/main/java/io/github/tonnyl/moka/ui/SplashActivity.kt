package io.github.tonnyl.moka.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.ui.auth.AuthActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as MokaApp).loginAccounts.observe(this) {
            val clazz = if (it.isEmpty()) {
                AuthActivity::class.java
            } else {
                MainActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, clazz))

            finish()
        }
    }

}