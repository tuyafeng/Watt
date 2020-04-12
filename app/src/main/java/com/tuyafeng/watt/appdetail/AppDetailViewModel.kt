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
import com.tuyafeng.watt.data.components.Component
import com.tuyafeng.watt.data.components.ComponentType
import com.tuyafeng.watt.data.components.FastComponentsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class AppDetailViewModel @Inject constructor(
    private val componentsRepository: FastComponentsRepository
) : ViewModel() {

    companion object {
        val TITLES: IntArray = intArrayOf(
            R.string.receivers,
            R.string.services,
            R.string.activities
        )
        val FILTERS: Array<ComponentType> = arrayOf(
            ComponentType.RECEIVER,
            ComponentType.SERVICE,
            ComponentType.ACTIVITY
        )
    }

    lateinit var pkg: String
    private var dataLoading = false
    private var dataChanged = false

    private val _components = MutableLiveData<List<Component>>().apply { value = emptyList() }
    val components: LiveData<List<Component>> get() = _components

    private val _applied = MutableLiveData(false)
    val applied: LiveData<Boolean> get() = _applied

    // Keep a list ready to toggle
    private val _opSet = MutableLiveData<Set<String>>().apply { value = emptySet() }
    val opSet: LiveData<Set<String>> = _opSet

    // new state
    var disableOp = true

    fun loadComponents() {
        if (dataLoading) return
        dataLoading = true
        viewModelScope.launch {
            _applied.postValue(componentsRepository.isRulesApplied(pkg))
            componentsRepository.getComponents(pkg) {
                _components.postValue(it)
            }
            dataLoading = false
        }
    }

    fun toggleComponentState(component: Component) {
        component.disabled.set(component.disabled.get() != true)
        dataChanged = true
    }

    fun toggleComponentsState(components: Set<String>): Int {
        var count = 0
        _components.value?.let { list ->
            list.asSequence().filter { components.contains(it.name) }.forEach {
                it.disabled.set(disableOp)
                count += 1
            }
        }
        if (count > 0) dataChanged = true
        return count
    }

    fun toggleAppliedState() {
        _applied.value = !(_applied.value ?: false)
        dataChanged = true
    }

    fun disablePreset() {
        disableOp = true
        _components.value?.let { list ->
            _opSet.value = list.asSequence()
                .filter {
                    it.disabled.get() == false && it.type != ComponentType.ACTIVITY
                            && inPresetList(it.name)
                }
                .map { it.name }
                .toSet()
        }
    }

    private fun inPresetList(name: String): Boolean {
        arrayListOf("push", ".ads.Activity").forEach {
            if (name.contains(it, true)) return true
        }
        return false
    }

    fun enableAll() {
        disableOp = false
        _components.value?.let { list ->
            _opSet.value = list.asSequence()
                .filter { it.disabled.get() == true }
                .map { it.name }
                .toSet()
        }
    }

    fun saveRules() {
        if (!dataChanged) return
        viewModelScope.launch {
            dataChanged = false
            componentsRepository.saveDisableComponents(
                pkg,
                _components.value?.filter { it.disabled.get() == true },
                (_applied.value == true)
            )
        }
    }

}

