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

package com.tuyafeng.watt.common

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.github.promeg.pinyinhelper.Pinyin
import com.tuyafeng.watt.data.App

fun PackageManager.queryPackage(pkg: String) : App? {
    try {
        val packageInfo: PackageInfo =
            this.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)
        return App().apply {
            packageName = packageInfo.packageName
            label = packageInfo.applicationInfo.loadLabel(this@queryPackage).toString()
            pinyin = Pinyin.toPinyin(label, "")
            disabled.set(!packageInfo.applicationInfo.enabled)
            system = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
            icon = packageInfo.applicationInfo.loadIcon(this@queryPackage)
            versionName = packageInfo.versionName
            versionCode = if (Build.VERSION.SDK_INT >= 28) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
            targetSdkVersion = packageInfo.applicationInfo.targetSdkVersion
        }
    } catch (ignored: PackageManager.NameNotFoundException) {}
    return null
}