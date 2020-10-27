/*
 * Copyright (C) 2020 Tu Yafeng
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.tuyafeng.watt.preference

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.jaredrummler.android.shell.Shell
import com.tuyafeng.watt.BuildConfig
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.Commands
import com.tuyafeng.watt.common.showSnackbar

class PreferenceFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    companion object {
        private const val PROJECT_URL = "https://github.com/tuyafeng/Watt"
        private const val FEEDBACK_URL = "https://github.com/tuyafeng/Watt/issues"
        private const val README_URL = "https://github.com/tuyafeng/Watt/blob/master/README.md"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindPreference()
    }

    private fun bindPreference() {
        arrayListOf(
            R.string.pref_restore,
            R.string.pref_dev,
            R.string.pref_server,
            R.string.pref_about,
            R.string.pref_licenses,
            R.string.pref_feedback
        ).forEach {
            findPreference<Preference?>(requireContext().getString(it))?.apply {
                onPreferenceClickListener = this@PreferenceFragment
            }
        }
        findPreference<Preference?>(requireContext().getString(R.string.pref_about))?.summary =
            "v${BuildConfig.VERSION_NAME}"
        findPreference<Preference?>(requireContext().getString(R.string.pref_server))?.isVisible =
            Build.VERSION.SDK_INT >= 21
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        if (preference == null) return false
        when (preference.key) {
            getString(R.string.pref_restore) -> restore()
            getString(R.string.pref_dev) -> openDevOptions()
            getString(R.string.pref_server) -> changeServer()
            getString(R.string.pref_about) -> openUrl(PROJECT_URL)
            getString(R.string.pref_licenses) -> openUrl(README_URL)
            getString(R.string.pref_feedback) -> openUrl(FEEDBACK_URL)
        }
        return true
    }

    private fun restore() {
        confirmToRunCommand(
            R.string.empty_all_applied_rules,
            R.string.empty_all_applied_rules_dialog,
            R.string.empty_all_applied_rules_done,
            Commands.removeAllRules()
        )
    }

    private fun openDevOptions() {
        startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun changeServer() {
        confirmToRunCommand(
            R.string.set_captive_portal_server,
            R.string.set_captive_portal_server_dialog,
            R.string.set_captive_portal_server_done,
            Commands.setCaptivePortalServer()
        )
    }

    private fun confirmToRunCommand(title: Int, message: Int, success: Int, command: String?) {
        if (command == null) return
        AlertDialog.Builder(requireContext()).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(android.R.string.ok) { _: DialogInterface, _: Int ->
                view?.showSnackbar(
                    if (Shell.SU.run(command).isSuccessful) success else R.string.failed_to_execute_command
                )
            }
            setNegativeButton(android.R.string.cancel, null)
        }.show()
    }

    private fun openUrl(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}