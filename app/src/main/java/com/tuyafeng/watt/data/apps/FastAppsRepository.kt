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

package com.tuyafeng.watt.data.apps

import com.tuyafeng.watt.di.ApplicationModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FastAppsRepository @Inject constructor(
    @ApplicationModule.AppsLocalDataSource private val localDataSource: AppsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppsRepository {

    private var cachedApps: MutableList<App> = mutableListOf()

    override suspend fun getApps(forceUpdate: Boolean, perform: (List<App>) -> Unit) {
        return withContext(ioDispatcher) {
            if (!forceUpdate and cachedApps.isNotEmpty()) {
                perform(cachedApps)
                return@withContext
            }
            cachedApps.clear()
            cachedApps.addAll(localDataSource.getInstalledApps())
            // First show the applications installed to display the list quickly
            perform(cachedApps)
            cachedApps.addAll(localDataSource.getSystemApps())
            perform(cachedApps)
        }
    }

}