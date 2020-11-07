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

import com.jaredrummler.android.shell.Shell
import com.tuyafeng.watt.common.ServiceChecker
import com.tuyafeng.watt.di.ApplicationModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FastPackagesRepository @Inject constructor(
    @ApplicationModule.PackagesLocalDataSource private val localDataSource: PackagesDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : PackagesRepository {

    private var cachedApps: MutableList<App> = mutableListOf()
    private var cachedPackage: String = ""
    private var cachedComponents: MutableList<Component> = mutableListOf()

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

    override suspend fun disableApp(pkg: String): Boolean {
        return withContext(ioDispatcher) {
            val result = localDataSource.disableApp(pkg)
            if (result) {
                cachedApps.find { it.packageName == pkg }?.disabled?.set(true)
            }
            result
        }
    }

    override suspend fun enableApp(pkg: String): Boolean {
        return withContext(ioDispatcher) {
            val result = localDataSource.enableApp(pkg)
            if (result) {
                cachedApps.find { it.packageName == pkg }?.disabled?.set(false)
            }
            result
        }
    }

    override suspend fun queryApp(pkg: String): App {
        return withContext(ioDispatcher) {
            localDataSource.queryApp(pkg)
        }
    }

    override suspend fun getComponents(forceUpdate: Boolean, pkg: String, perform: (List<Component>) -> Unit) {
        return withContext(ioDispatcher) {
            if (!forceUpdate && cachedPackage == pkg) {
                perform(cachedComponents)
                return@withContext
            }
            cachedComponents.apply {
                clear()
                addAll(localDataSource.getServices(pkg).apply {
                    ServiceChecker.update(pkg)
                    forEach {
                        if (ServiceChecker.isRunning(it.name)) it.running.set(true)
                    }
                })
                perform(this)
                addAll(localDataSource.getReceivers(pkg))
                addAll(localDataSource.getActivities(pkg))
                addAll(localDataSource.getProvider(pkg))
                perform(this)
                cachedPackage = pkg
            }
        }
    }

    override suspend fun disableComponent(pkg: String, name: String): Boolean {
        return withContext(ioDispatcher) {
            val result = localDataSource.disableComponent(pkg, name)
            if (result && pkg == cachedPackage) {
                cachedComponents.find { it.name == name }?.disabled?.set(true)
            }
            result
        }
    }

    override suspend fun enableComponent(pkg: String, name: String): Boolean {
        return withContext(ioDispatcher) {
            val result = localDataSource.enableComponent(pkg, name)
            if (result && pkg == cachedPackage) {
                cachedComponents.find { it.name == name }?.disabled?.set(false)
            }
            result
        }
    }

    override suspend fun restoreComponents(pkg: String): Boolean {
        if (pkg != cachedPackage) return false
        return withContext(ioDispatcher) {
            val commandBuilder = StringBuilder()
            cachedComponents.forEach {
                commandBuilder.append("pm default-state $pkg/${it.name}; ")
            }
            Shell.SU.run(commandBuilder.toString())
            true
        }
    }

}