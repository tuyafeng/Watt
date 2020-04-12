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

import com.tuyafeng.watt.common.ServiceChecker
import com.tuyafeng.watt.di.ApplicationModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FastComponentsRepository @Inject constructor(
    @ApplicationModule.ComponentsLocalDataSource private val localDataSource: ComponentsDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ComponentsRepository {

    private var cachedComponents: MutableList<Component> = mutableListOf()

    override suspend fun getComponents(pkg: String, perform: (List<Component>) -> Unit) {
        return withContext(ioDispatcher) {
            if (cachedComponents.isNotEmpty()) {
                perform(cachedComponents)
                return@withContext
            }
            cachedComponents.apply {
                clear()
                addAll(localDataSource.getReceivers(pkg))
                val actions = localDataSource.getReceiversActions(pkg)
                perform(this)
                asSequence()
                    .filter { actions[it.name] != null }
                    .forEach { it.extra = actions[it.name]!!.joinToString(separator = "\n") { action-> action } }
                val services = localDataSource.getServices(pkg).apply {
                    ServiceChecker.update(pkg)
                    forEach { if (ServiceChecker.isRunning(it.name)) it.running.set(true) }
                }
                addAll(services)
                addAll(localDataSource.getActivities(pkg))
                perform(this)
                val disabled = localDataSource.getDisabledComponentNames(pkg)
                asSequence()
                    .filter { disabled.contains(it.name) }
                    .forEach { it.disabled.set(true) }
                perform(this)
            }
        }
    }

    override suspend fun saveDisableComponents(pkg: String, disabledComponents: List<Component>?, activated: Boolean) {
        disabledComponents?.let {
            withContext(ioDispatcher) {
                localDataSource.saveDisabledComponents(pkg, it)
                localDataSource.applyRules(pkg, activated)
            }
        }
    }

    override suspend fun isRulesApplied(pkg: String): Boolean = localDataSource.isRulesApplied(pkg)

}