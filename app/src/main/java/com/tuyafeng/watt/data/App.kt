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

package com.tuyafeng.watt.data

import android.graphics.drawable.Drawable
import androidx.databinding.ObservableField

data class App(
    var packageName: String = "",
    var label: String = "",
    var icon: Drawable? = null,
    var disabled: ObservableField<Boolean> = ObservableField<Boolean>(false),
    var system: Boolean = false,
    var pinyin: String = "",
    var versionName: String = "",
    var versionCode: Long = 0L,
    var targetSdkVersion: Int = 0
)