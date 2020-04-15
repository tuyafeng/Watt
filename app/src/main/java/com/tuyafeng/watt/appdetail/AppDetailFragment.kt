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
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.setupSnackbar
import com.tuyafeng.watt.common.setupToolbar
import com.tuyafeng.watt.common.showSnackbar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.appdetail_frag.*
import javax.inject.Inject

class AppDetailFragment : DaggerFragment(), Toolbar.OnMenuItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by this.run { viewModels<AppDetailViewModel> { viewModelFactory } }

    private val args: AppDetailFragmentArgs by navArgs()

    private lateinit var applyMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.pkg = args.pkg
        viewModel.loadComponents()
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

    @SuppressLint("RestrictedApi")
    private fun setupToolbar() {
        setupToolbar(view?.findViewById(R.id.toolbar), true) {
            title = args.label
            val params: AppBarLayout.LayoutParams = this.layoutParams as AppBarLayout.LayoutParams
            params.scrollFlags = (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                    or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
            inflateMenu(R.menu.menu_components)
            if (menu is MenuBuilder) {
                (menu as MenuBuilder).setOptionalIconsVisible(true)
            }
            setOnMenuItemClickListener(this@AppDetailFragment)
            applyMenuItem = menu.findItem(R.id.menu_apply)
            viewModel.applied.observe(this@AppDetailFragment.viewLifecycleOwner, Observer {
                setAppliedMenuItem(it)
            })
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_apply -> viewModel.toggleAppliedState()
            R.id.menu_help -> openHelp()
            R.id.menu_disable_preset -> viewModel.disablePreset()
            R.id.menu_enable_all -> viewModel.enableAll()
            R.id.menu_app_info -> openAppInfo()
        }
        return true
    }

    private fun setAppliedMenuItem(applied: Boolean) {
        applyMenuItem.apply {
            val title = if (applied) R.string.rules_applied else R.string.rules_unapplied
            setTitle(title)
            setIcon(if (applied) R.drawable.ic_action_applied else R.drawable.ic_action_unapplied)
            icon.colorFilter = if (applied) PorterDuffColorFilter(
                ContextCompat.getColor(requireContext(), R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP
            ) else null
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this.viewLifecycleOwner, viewModel.snackbarMessage)
    }

    private fun setupPager() {
        pager.adapter = ComponentsCollectionAdapter(this)
        TabLayoutMediator(tabs, pager) { tab, position ->
            tab.setText(AppDetailViewModel.TITLES[position])
        }.attach()
        viewModel.opSet.observe(this.viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) {
                dialogBeforeToggle(it, viewModel.disableOp)
            }
        })
    }

    private fun dialogBeforeToggle(set: Set<String>, disabled: Boolean) {
        AlertDialog.Builder(requireContext()).apply {
            val names = set.toTypedArray()
            val vals = BooleanArray(names.size) { true }
            setTitle(if (disabled) R.string.disable_selected else R.string.enable_selected)
            setMultiChoiceItems(names, vals) { _, which, isChecked -> vals[which] = isChecked }
            setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { _, _ ->
                val count = viewModel.toggleComponentsState(set.asSequence()
                    .withIndex().filter { vals[it.index] }.map { it.value }.toSet()
                )
                if (count > 0) {
                    view?.showSnackbar(
                        requireContext().resources.getQuantityString(
                            (if (disabled) R.plurals.disabled_selected_done else R.plurals.enabled_selected_done)
                            , count, count
                        )
                    )
                }
            })
            setNegativeButton(android.R.string.cancel, null)
        }.show()
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

    override fun onPause() {
        viewModel.saveRules()
        super.onPause()
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