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

package com.tuyafeng.watt.apps

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.queryPackage
import com.tuyafeng.watt.data.App
import kotlinx.android.synthetic.main.app_menu_frag.*


class AppMenuFragment : BottomSheetDialogFragment() {

    private val args: AppMenuFragmentArgs by navArgs()
    private lateinit var app: App

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.app_menu_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().packageManager.queryPackage(args.pkg)?.let {
            app = it
            setupViews()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog)
        }
        return dialog
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun setWhiteNavigationBar(dialog: Dialog) {
        dialog.window?.let { window ->
            val metrics = DisplayMetrics()
            window.windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(requireActivity().window.navigationBarColor)
            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }
    }

    private fun setupViews() {
        val isDisabled = app.disabled.get() == true
        tv_app_label.text = app.label
        iv_app_icon.setImageDrawable(app.icon)
        tv_disable_app.text =
            getString(if (isDisabled) R.string.enable_app else R.string.disable_app)
        tv_disable_app.setOnClickListener {
            dismiss()
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                if (isDisabled) ENABLE else DISABLE, app.packageName)
        }
        tv_view_in_settings.setOnClickListener {
            try {
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${app.packageName}")
                })
            } catch (ignored: ActivityNotFoundException) {}
            dismiss()
        }
        tv_view_in_app_market.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("market://details?id=${app.packageName}")
                })
            } catch (ignored: ActivityNotFoundException) {}
            dismiss()
        }
        if (app.system) {
            tv_uninstall.isClickable = false
            tv_uninstall.isFocusable = false
            tv_uninstall.alpha = 0.5f
        } else {
            tv_uninstall.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.parse("package:${app.packageName}")
                    })
                } catch (ignored: ActivityNotFoundException) {}
                dismiss()
            }
        }

    }

    companion object {
        const val DISABLE = "disable"
        const val ENABLE = "enable"
    }
}