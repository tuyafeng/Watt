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

package com.tuyafeng.watt.data

import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ComponentInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.github.promeg.pinyinhelper.Pinyin
import com.jaredrummler.android.shell.Shell
import com.tuyafeng.watt.BuildConfig
import com.tuyafeng.watt.common.Commands

class PackagesLocalDataSource internal constructor(
    private val packageManager: PackageManager
) : PackagesDataSource {

    override suspend fun getInstalledApps(): List<App> = getApps(true)

    override suspend fun getSystemApps(): List<App> = getApps(false)

    private fun getApps(installed: Boolean): List<App> {
        return packageManager.getInstalledPackages(0)
            .asSequence()
            .filter { (it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) == installed
                    && (it.packageName != "android" && it.packageName != BuildConfig.APPLICATION_ID)}
            .map { packageInfo ->
                App().apply {
                    packageName = packageInfo.packageName
                    label = packageInfo.applicationInfo.loadLabel(packageManager).toString()
                    pinyin = Pinyin.toPinyin(label, "")
                    disabled.set(!packageInfo.applicationInfo.enabled)
                    system = packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    icon = packageInfo.applicationInfo.loadIcon(packageManager)
                }
            }
            .toList()
    }

    override suspend fun queryApp(pkg: String): App {
        return App().apply {
            getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)?.let {
                packageName = it.packageName
                label = it.applicationInfo.loadLabel(packageManager).toString()
                pinyin = Pinyin.toPinyin(label, "")
                icon = it.applicationInfo.loadIcon(packageManager)
                versionName = it.versionName
                versionCode = if (Build.VERSION.SDK_INT >= 28) it.longVersionCode else it.versionCode.toLong()
                targetSdkVersion = it.applicationInfo.targetSdkVersion
            }
        }
    }

    override suspend fun disableApp(pkg: String): Boolean = Shell.SU.run(Commands.disableApp(pkg)).isSuccessful

    override suspend fun enableApp(pkg: String): Boolean = Shell.SU.run(Commands.enableApp(pkg)).isSuccessful

    override suspend fun getActivities(pkg: String): List<Component> =
        getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)?.activities?.let {
            it.asSequence().map {
                Component().apply {
                    this.name = it.name
                    this.disabled.set(isComponentDisable(it))
                    this.type = ComponentType.ACTIVITY
                }
            }.toList()
        } ?: emptyList()

    override suspend fun getServices(pkg: String): List<Component> =
        getPackageInfo(pkg, PackageManager.GET_SERVICES)?.services?.let {
            it.asSequence().map {
                Component().apply {
                    this.name = it.name
                    this.disabled.set(isComponentDisable(it))
                    this.type = ComponentType.SERVICE
                }
            }.toList()
        } ?: emptyList()

    override suspend fun getReceivers(pkg: String): List<Component> {
        val actions = packageManager.queryBroadcastReceivers(
            Intent().setPackage(pkg), PackageManager.GET_RESOLVED_FILTER
        ).asSequence()
            .filter { it.filter.countActions() > 0 }
            .map {
                it.activityInfo.name to Array(it.filter.countActions()) { idx ->
                    it.filter.getAction(
                        idx
                    )
                }
            }
            .toMap()
        return getPackageInfo(pkg, PackageManager.GET_RECEIVERS)?.receivers?.let {
            it.asSequence().map {
                Component().apply {
                    this.name = it.name
                    this.disabled.set(isComponentDisable(it))
                    actions[it.name]?.let { action ->
                        this.extra = action.joinToString(separator = "\n")
                    }
                    this.type = ComponentType.RECEIVER
                }
            }.toList()
        } ?: emptyList()
    }

    override suspend fun getProvider(pkg: String): List<Component> =
        getPackageInfo(pkg, PackageManager.GET_PROVIDERS)?.providers?.let {
            it.asSequence().map {
                Component().apply {
                    this.name = it.name
                    this.disabled.set(isComponentDisable(it))
                    this.type = ComponentType.PROVIDER
                }
            }.toList()
        } ?: emptyList()

    private fun getPackageInfo(pkg: String, flag: Int): PackageInfo? {
        val flags: Int = flag or (if (Build.VERSION.SDK_INT < 24)
            PackageManager.GET_DISABLED_COMPONENTS else PackageManager.MATCH_DISABLED_COMPONENTS)
        return try {
            packageManager.getPackageInfo(pkg, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun isComponentDisable(info: ComponentInfo) : Boolean {
        return when (packageManager.getComponentEnabledSetting(ComponentName(info.packageName, info.name))) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER, PackageManager.COMPONENT_ENABLED_STATE_DISABLED -> return true
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> return false
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> return !info.enabled
            else -> false
        }
    }

    override suspend fun disableComponent(pkg: String, name: String): Boolean = Shell.SU.run(
        Commands.disableComponent(
            pkg,
            name
        )
    ).isSuccessful

    override suspend fun enableComponent(pkg: String, name: String): Boolean = Shell.SU.run(
        Commands.enableComponent(
            pkg,
            name
        )
    ).isSuccessful

    override suspend fun restoreComponentState(pkg: String, name: String): Boolean = Shell.SU.run(
        Commands.restoreComponent(
            pkg,
            name
        )
    ).isSuccessful
}