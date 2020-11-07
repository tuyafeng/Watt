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

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.EventObserver
import com.tuyafeng.watt.common.setupSnackbar
import com.tuyafeng.watt.common.setupToolbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.appdetail_frag.*
import javax.inject.Inject

class AppDetailFragment : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by this.run { viewModels<AppDetailViewModel> { viewModelFactory } }

    private val args: AppDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.pkg = args.pkg
        viewModel.loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.appdetail_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setupSnackbar()
        setupPager()
    }

    @SuppressLint("RestrictedApi", "SetTextI18n")
    private fun setupToolbar() {
        setupToolbar(view?.findViewById(R.id.toolbar), true) {
            toolbar_layout.title = args.label
            //toolbar_layout.setExpandedTitleColor(Color.TRANSPARENT)
            title = args.label
            inflateMenu(R.menu.menu_components)
            setOnMenuItemClickListener(this@AppDetailFragment)
        }
        viewModel.appDetailEvent.observe(this.viewLifecycleOwner, EventObserver {
            //tv_app_name.text = it.label
            tv_package_name.text = it.packageName
            tv_app_version.text = "${it.versionName}(${it.versionCode})"
            tv_app_target_sdk.text = "API ${it.targetSdkVersion}"
            iv_app_icon.setImageDrawable(it.icon)
        })
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_help -> openHelp()
            R.id.menu_restore -> {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle(R.string.restore)
                    setMessage(R.string.restore_message)
                    setCancelable(true)
                    setPositiveButton(android.R.string.ok) { _, _ ->
                        viewModel.restoreComponents()
                    }
                    setNegativeButton(android.R.string.cancel, null)
                }.show()
            }
            R.id.menu_app_info -> openAppInfo()
        }
        return true
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this.viewLifecycleOwner, viewModel.snackbarMessage)
    }

    private fun setupPager() {
        pager.adapter = ComponentsCollectionAdapter(this)
        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.setText(AppDetailViewModel.TITLES[position])
        }.attach()
    }

    private fun openAppInfo() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", args.pkg, null))
        requireActivity().startActivity(intent)
    }

    private fun openHelp() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.help)
            setMessage(R.string.help_content_components)
            setCancelable(true)
            setPositiveButton(android.R.string.ok, null)
        }.show()
    }

    private class ComponentsCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = AppDetailViewModel.TITLES.size

        override fun createFragment(position: Int): Fragment {
            return ComponentFragment().apply {
                arguments = Bundle().apply { putInt(ComponentFragment.KEY_POSITION, position) }
            }
        }
    }

}