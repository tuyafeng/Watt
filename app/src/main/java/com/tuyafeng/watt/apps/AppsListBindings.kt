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

import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tuyafeng.watt.common.dp
import com.tuyafeng.watt.data.apps.App

private val iconRect: Rect by lazy {
    Rect(0, 0, 32.dp, 32.dp)
}

@BindingAdapter("items")
fun setItems(listView: RecyclerView, items: List<App>) {
    (listView.adapter as AppsAdapter).submitList(items)
}

@BindingAdapter("logo")
fun setLogo(textView: TextView, logo: Drawable?) {
    textView.setCompoundDrawablesRelative(logo?.apply {
        bounds = iconRect
    },null,null,null)
}