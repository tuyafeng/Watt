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

package com.tuyafeng.watt.data.components

import androidx.databinding.ObservableField

data class Component(
    var name: String = "",
    var extra: String = ""
) : Comparable<Component> {
    var simpleName: String = ""
        private set(value) {}
        get() {
            if (field.isEmpty() && name.isNotEmpty()) {
                field = name.substringAfterLast('.')
            }
            return field
        }

    var disabled = ObservableField<Boolean>(false)

    var running = ObservableField<Boolean>(false)

    var type: ComponentType = ComponentType.UNKNOWN

    var flag: Int
        private set(value) {}
        get() = (if (disabled.get() == true) 0b1 else 0) or (if (running.get() == true) 0b10 else 0)

    override fun compareTo(other: Component): Int = compareValuesBy(
        this,
        other,
        { it.running.get() == false },
        { it.disabled.get() == false },
        { it.name })

}

enum class ComponentType {
    ACTIVITY,
    SERVICE,
    RECEIVER,
    PROVIDER,
    UNKNOWN
}