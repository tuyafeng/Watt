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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.tuyafeng.watt.R
import com.tuyafeng.watt.common.updateList
import com.tuyafeng.watt.data.components.ComponentType
import kotlinx.android.synthetic.main.components_frag.*

class ComponentFragment : Fragment() {

    companion object {
        const val KEY_POSITION = "pos"
    }

    private val viewModel by lazy {
        parentFragment?.run { ViewModelProvider(this).get(AppDetailViewModel::class.java) }
    }

    private val showType: ComponentType by lazy {
        AppDetailViewModel.FILTERS[requireArguments().getInt(KEY_POSITION)]
    }

    private lateinit var listAdapter: ComponentsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.components_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel?.let { model ->
            listAdapter = ComponentsAdapter(model)
            components_list.adapter = listAdapter
            model.components.observe(this.viewLifecycleOwner, Observer { components ->
                val list = components.asSequence()
                    .filter { it.type == showType }
                    .sorted()
                    .toList()
                listAdapter.updateList(list)
                empty_view.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE
            })
        }
    }
}