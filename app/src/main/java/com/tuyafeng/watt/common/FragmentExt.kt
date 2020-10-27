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

package com.tuyafeng.watt.common

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tuyafeng.watt.R


fun Fragment.setupToolbar(
    toolbar: Toolbar?,
    withBack: Boolean = false,
    action: Toolbar.() -> Unit
) {
    toolbar?.apply {
        if (withBack) {
            navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_action_back).also {
                    it?.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(requireContext(), R.color.colorIconTint),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
        action(this)
    }
}

fun Fragment.defaultNavOptions(): NavOptions? {
    return NavOptions.Builder()
        .setEnterAnim(R.anim.nav_default_enter_anim)
        .setExitAnim(R.anim.nav_default_exit_anim)
        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        .build()
}