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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.EventObserver
import com.tuyafeng.watt.common.defaultNavOptions
import com.tuyafeng.watt.common.setupToolbar
import com.tuyafeng.watt.data.apps.App
import com.tuyafeng.watt.databinding.AppsFragBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class AppsFragment : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<AppsViewModel> { viewModelFactory }
    private lateinit var viewDataBinding: AppsFragBinding
    private lateinit var listAdapter: AppsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = AppsFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupToolbar()
        setupListAdapter()
        setupNavigation()
    }

    @SuppressLint("RestrictedApi")
    private fun setupToolbar() {
        setupToolbar(view?.findViewById(R.id.toolbar)) {
            title = getString(R.string.app_name)
            inflateMenu(R.menu.menu_apps)
            if (menu is MenuBuilder) {
                (menu as MenuBuilder).setOptionalIconsVisible(true)
            }
            setOnMenuItemClickListener(this@AppsFragment)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.menu_filter_installed, R.id.menu_filter_system,
        R.id.menu_filter_disabled, R.id.menu_filter_all -> {
            viewModel.apply {
                setFiltering(
                    when (item.itemId) {
                        R.id.menu_filter_all -> AppsFilterType.ALL_APPS
                        R.id.menu_filter_disabled -> AppsFilterType.DISABLED_APPS
                        R.id.menu_filter_system -> AppsFilterType.SYSTEM_APPS
                        else -> AppsFilterType.INSTALLED_APPS
                    }
                )
                loadApps(false)
            }
            true
        }
        R.id.menu_reload -> {
            viewModel.loadApps(true)
            true
        }
        R.id.menu_settings -> {
            openPreference()
            true
        }
        else -> false
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = AppsAdapter(viewModel)
            viewDataBinding.appsList.apply {
                itemAnimator = null
                adapter = listAdapter
            }
        }
    }

    private fun setupNavigation() {
        viewModel.openAppEvent.observe(this.viewLifecycleOwner, EventObserver {
            openAppDetail(it)
        })
    }

    private fun openAppDetail(app: App) {
        val action =
            AppsFragmentDirections.actionAppsFragmentToAppDetailFragment(app.packageName, app.label)
        findNavController().navigate(action, defaultNavOptions())
    }

    private fun openPreference() {
        val action = AppsFragmentDirections.actionAppsFragmentToPreferenceWrapperFragment()
        findNavController().navigate(action, defaultNavOptions())
    }

}
