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

interface PackagesRepository {

    suspend fun getApps(forceUpdate: Boolean, perform: (List<App>) -> Unit)

    suspend fun queryApp(pkg: String): App

    suspend fun disableApp(pkg: String): Boolean

    suspend fun enableApp(pkg: String): Boolean

    suspend fun getComponents(forceUpdate: Boolean, pkg: String, perform: (List<Component>) -> Unit)

    suspend fun disableComponent(pkg: String, name: String): Boolean

    suspend fun enableComponent(pkg: String, name: String): Boolean

    suspend fun restoreComponents(pkg: String): Boolean
}