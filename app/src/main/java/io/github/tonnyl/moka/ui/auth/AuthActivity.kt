package io.github.tonnyl.moka.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.github.tonnyl.moka.databinding.ActivityAuthBinding
import io.github.tonnyl.moka.util.HeightTopWindowInsetsListener
import io.github.tonnyl.moka.util.NoopWindowInsetsListener
import io.github.tonnyl.moka.util.updateForTheme

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateForTheme()

        binding = ActivityAuthBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.contentContainer.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        binding.contentContainer.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)

        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)
    }

}