package io.github.tonnyl.moka.ui.about

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.github.tonnyl.moka.R

class AboutPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.about_screen)
    }

}