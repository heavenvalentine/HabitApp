package com.dicoding.habitapp.setting

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.dicoding.habitapp.R

class SettingsActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Notifications permission granted")
            } else {
                showToast("Notifications will not show without permission")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (Build.VERSION.SDK_INT > 32) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            //TODO NOTIF MASI BERMASALAH
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            //TODO 11 : DONE Update theme based on value in ListPreference
            val darkModePreference = findPreference<ListPreference>(getString(R.string.pref_key_dark))
            darkModePreference?.setOnPreferenceChangeListener { _, newValue ->
                val themeMode = when (newValue.toString()) {
                    getString(R.string.pref_dark_off) -> AppCompatDelegate.MODE_NIGHT_NO
                    getString(R.string.pref_dark_on) -> AppCompatDelegate.MODE_NIGHT_YES
                    getString(R.string.dark_mode_auto) -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                updateTheme(themeMode)
                true
            }
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }
    }
}