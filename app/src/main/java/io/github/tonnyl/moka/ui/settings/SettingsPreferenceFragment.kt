package io.github.tonnyl.moka.ui.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R

class SettingsPreferenceFragment : PreferenceFragmentCompat(),
    Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_screen)

        findPreference<Preference>("key_enable_notifications")?.onPreferenceChangeListener = this
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        preference ?: return false
        newValue ?: return false
        when (preference.key) {
            "key_enable_notifications" -> {
                if (newValue is Boolean) {
                    (requireContext().applicationContext as MokaApp).triggerNotificationWorker(
                        newValue
                    )
                }
            }
        }

        return true
    }
}