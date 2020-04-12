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

import androidx.lifecycle.*
import com.tuyafeng.watt.common.Event
import com.tuyafeng.watt.data.apps.App
import com.tuyafeng.watt.data.apps.FastAppsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppsViewModel @Inject constructor(
    private val appsRepository: FastAppsRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<App>>().apply { value = emptyList() }
    val items: LiveData<List<App>> = _items

    val empty: LiveData<Boolean> = Transformations.map(_items) { it.isEmpty() }

    private var _currentFiltering = AppsFilterType.INSTALLED_APPS

    private val _openAppEvent = MutableLiveData<Event<App>>()
    val openAppEvent: LiveData<Event<App>> = _openAppEvent

    private var dataLoading = false

    init {
        setFiltering(AppsFilterType.INSTALLED_APPS)
        loadApps(true)
    }

    fun loadApps(forceUpdate: Boolean) {
        if (dataLoading) {
            return
        }
        dataLoading = true
        viewModelScope.launch {
            appsRepository.getApps(forceUpdate) {
                showApps(it)
            }
            dataLoading = false
        }
    }

    private fun showApps(apps: List<App>) {
        _items.postValue(apps.asSequence().filter {
            when (_currentFiltering) {
                AppsFilterType.INSTALLED_APPS -> !it.system
                AppsFilterType.SYSTEM_APPS -> it.system
                AppsFilterType.DISABLED_APPS -> it.disabled
                else -> true
            }
        }.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.pinyin })).toList())
    }

    fun setFiltering(requestType: AppsFilterType) {
        _currentFiltering = requestType
    }

    fun openAppDetail(app: App) {
        _openAppEvent.value = Event(app)
    }
}
