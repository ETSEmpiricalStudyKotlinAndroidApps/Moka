package io.github.tonnyl.moka.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.tonnyl.moka.R

class UserProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        val fragment = UserProfileFragment.newInstance("tonnyl")
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
    }

}