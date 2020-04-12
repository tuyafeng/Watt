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

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tuyafeng.watt.R
import com.tuyafeng.watt.data.components.Component
import com.tuyafeng.watt.databinding.ComponentItemBinding

class ComponentsAdapter(private val viewModel: AppDetailViewModel) :
    ListAdapter<Component, ComponentsAdapter.ViewHolder>(ComponentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val component = getItem(position)
        holder.bind(viewModel, component)
    }

    class ViewHolder constructor(private val binding: ComponentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: AppDetailViewModel, item: Component) {
            binding.viewmodel = viewModel
            binding.component = item
            binding.componentName.text = SpannableStringBuilder(item.name).apply {
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    item.name.lastIndexOf('.') + 1, item.name.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (item.running.get() == true) {
                    setSpan(
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                binding.componentName.context,
                                R.color.colorAccent
                            )
                        ),
                        item.name.lastIndexOf('.') + 1, item.name.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ComponentItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ComponentDiffCallback : DiffUtil.ItemCallback<Component>() {

    override fun areItemsTheSame(oldItem: Component, newItem: Component): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Component, newItem: Component): Boolean {
        return oldItem.name == newItem.name && oldItem.disabled.get() == newItem.disabled.get()
    }
}
