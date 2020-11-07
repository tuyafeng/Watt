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

interface PackagesDataSource {

    suspend fun getInstalledApps(): List<App>

    suspend fun getSystemApps(): List<App>

    suspend fun queryApp(pkg: String): App

    suspend fun disableApp(pkg: String): Boolean

    suspend fun enableApp(pkg: String): Boolean

    suspend fun getReceivers(pkg: String): List<Component>

    suspend fun getActivities(pkg: String): List<Component>

    suspend fun getServices(pkg: String): List<Component>

    suspend fun getProvider(pkg: String): List<Component>

    suspend fun disableComponent(pkg: String, name: String): Boolean

    suspend fun enableComponent(pkg: String, name: String): Boolean

    suspend fun restoreComponentState(pkg: String, name: String): Boolean

}