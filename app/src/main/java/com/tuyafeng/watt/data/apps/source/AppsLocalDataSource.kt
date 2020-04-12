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

package com.tuyafeng.watt.data.apps.source

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.github.promeg.pinyinhelper.Pinyin
import com.tuyafeng.watt.BuildConfig
import com.tuyafeng.watt.data.apps.App
import com.tuyafeng.watt.data.apps.AppsDataSource

class AppsLocalDataSource internal constructor(
    private val packageManager: PackageManager
) : AppsDataSource {

    override suspend fun getInstalledApps(): List<App> {
        return getApps(true)
    }

    override suspend fun getSystemApps(): List<App> {
        return getApps(false)
    }

    private fun getApps(installed: Boolean): List<App> {
        return packageManager.getInstalledPackages(0)
            .asSequence()
            .filterNot { it.packageName == "android" || it.packageName == BuildConfig.APPLICATION_ID }
            .filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) == installed }
            .map { packageInfo ->
                App().apply {
                    packageName = packageInfo.packageName
                    label = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                    pinyin = Pinyin.toPinyin(label, "")
                    disabled = !packageInfo.applicationInfo.enabled
                    system = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    icon = packageInfo.applicationInfo.loadIcon(packageManager)
                }
            }
            .toList()
    }

}