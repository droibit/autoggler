package com.droibit.autoggler.settings

import android.os.Bundle
import android.support.v7.preference.PreferenceFragmentCompat
import com.droibit.autoggler.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }
}