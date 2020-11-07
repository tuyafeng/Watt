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

package com.tuyafeng.watt.appdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.Event
import com.tuyafeng.watt.data.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppDetailViewModel @Inject constructor(
    private val componentsRepository: FastPackagesRepository
) : ViewModel() {

    companion object {
        val TITLES: IntArray = intArrayOf(
            R.string.services,
            R.string.receivers,
            R.string.activities,
            R.string.providers
        )
        val FILTERS: Array<ComponentType> = arrayOf(
            ComponentType.SERVICE,
            ComponentType.RECEIVER,
            ComponentType.ACTIVITY,
            ComponentType.PROVIDER
        )
    }

    lateinit var pkg: String

    private val _components = MutableLiveData<List<Component>>().apply { value = emptyList() }
    val components: LiveData<List<Component>> get() = _components

    private val _appDetailEvent = MutableLiveData<Event<App>>()
    val appDetailEvent: LiveData<Event<App>> = _appDetailEvent

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    fun loadData() {
        viewModelScope.launch {
            _appDetailEvent.postValue(Event(componentsRepository.queryApp(pkg)))
            componentsRepository.getComponents(true, pkg) {
                _components.postValue(it)
            }
        }
    }

    fun toggleComponentState(component: Component) {
        viewModelScope.launch {
            val newState = component.disabled.get() != true
            if (newState && componentsRepository.disableComponent(pkg, component.name)) {
                component.disabled.set(true)
            } else if (!newState && componentsRepository.enableComponent(pkg, component.name)) {
                component.disabled.set(false)
            }
        }
    }

    fun restoreComponents() {
        viewModelScope.launch {
            if (componentsRepository.restoreComponents(pkg)) {
                _snackbarText.value = Event(R.string.restore_done)
                componentsRepository.getComponents(true, pkg) {
                    _components.postValue(it)
                }
            }
        }
    }

}

