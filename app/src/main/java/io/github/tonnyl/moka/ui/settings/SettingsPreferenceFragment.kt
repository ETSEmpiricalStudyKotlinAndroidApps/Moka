package io.github.tonnyl.moka.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.github.tonnyl.moka.R

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)
    }

}