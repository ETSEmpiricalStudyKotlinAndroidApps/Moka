package io.github.tonnyl.moka.ui.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.util.updateForTheme

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        updateForTheme()
    }

}