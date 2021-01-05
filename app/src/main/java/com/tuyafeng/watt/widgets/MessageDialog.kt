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

package com.tuyafeng.watt.widgets

import android.app.Dialog
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.goneIf
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.message_dialog.view.*

/**
 * This is a very simple message dialog
 */
class MessageDialog : BottomSheetDialogFragment() {

    companion object {
        private const val KEY_PARAMS = "params"

        fun newInstance(params: Params): MessageDialog {
            val args = Bundle()
            args.putParcelable(KEY_PARAMS, params)
            val fragment = MessageDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val params: Params by lazy {
        arguments?.getParcelable(KEY_PARAMS) ?: Params()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Params>(KEY_PARAMS)
        view.tv_dialog_title.let {
            it.goneIf { params.title.isNullOrEmpty() }
            it.text = params.title
        }
        view.tv_dialog_message.let {
            it.goneIf { params.message.isNullOrEmpty() }
            it.text = params.message
        }
        view.btn_dialog_positive.let {
            it.goneIf { params.positiveButton.isNullOrEmpty() }
            it.text = params.positiveButton
            it.setOnClickListener {
                params.positiveCallback?.invoke()
                dismiss()
            }
        }
        view.btn_dialog_negative.let {
            it.goneIf { params.negativeButton.isNullOrEmpty() }
            it.text = params.negativeButton
            it.setOnClickListener {
                params.negativeCallback?.invoke()
                dismiss()
            }
        }
        view.btn_dialog_neutral.let {
            it.goneIf { params.neutralButton.isNullOrEmpty() }
            it.text = params.neutralButton
            it.setOnClickListener {
                params.neutralCallback?.invoke()
                dismiss()
            }
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



    class Builder {

        private val params = Params()

        fun setTitle(title: String?): Builder {
            params.title = title
            return this
        }

        fun setMessage(message: String?): Builder {
            params.message = message
            return this
        }

        fun setPositiveButton(button: String?, callback: (() -> Unit)?): Builder {
            params.positiveButton = button
            params.positiveCallback = callback
            return this
        }

        fun setNegativeButton(button: String?, callback: (() -> Unit)?): Builder {
            params.negativeButton = button
            params.negativeCallback = callback
            return this
        }

        fun setNeutralButton(button: String?, callback: (() -> Unit)?): Builder {
            params.neutralButton = button
            params.neutralCallback = callback
            return this
        }

        fun create(): MessageDialog {
            return newInstance(params)
        }
    }

    @Parcelize
    class Params : Parcelable {
        var title: String? = null
        var message: String? = null
        var positiveButton: String? = null
        var positiveCallback: (() -> Unit)? = null
        var negativeButton: String? = null
        var negativeCallback: (() -> Unit)? = null
        var neutralButton: String? = null
        var neutralCallback: (() -> Unit)? = null
    }

}