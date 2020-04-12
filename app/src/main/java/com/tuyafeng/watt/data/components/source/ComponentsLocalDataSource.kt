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

package com.tuyafeng.watt.data.components.source

import android.content.Intent
import android.content.pm.ComponentInfo
import android.content.pm.PackageManager
import com.jaredrummler.android.shell.Shell
import com.tuyafeng.watt.data.components.Component
import com.tuyafeng.watt.data.components.ComponentType
import com.tuyafeng.watt.data.components.ComponentsDataSource
import java.io.File

class ComponentsLocalDataSource internal constructor(
    private val localIfwRulesPath: String,
    private val packageManager: PackageManager
) : ComponentsDataSource {

    companion object {
        private const val IFW_DATA = "/data/system/ifw/"
    }

    override suspend fun getActivities(pkg: String): List<Component> {
        return getComponents(
            packageManager.getPackageInfo(
                pkg,
                PackageManager.GET_ACTIVITIES
            ).activities, ComponentType.ACTIVITY
        )
    }

    override suspend fun getServices(pkg: String): List<Component> {
        return getComponents(
            packageManager.getPackageInfo(
                pkg,
                PackageManager.GET_SERVICES
            ).services, ComponentType.SERVICE
        )
    }

    override suspend fun getReceivers(pkg: String): List<Component> {
        return getComponents(
            packageManager.getPackageInfo(
                pkg,
                PackageManager.GET_RECEIVERS
            ).receivers, ComponentType.RECEIVER
        )
    }

    override suspend fun getProvider(pkg: String): List<Component> {
        return getComponents(
            packageManager.getPackageInfo(
                pkg,
                PackageManager.GET_PROVIDERS
            ).providers, ComponentType.PROVIDER
        )
    }

    private fun getComponents(
        componentInfos: Array<out ComponentInfo>?,
        type: ComponentType
    ): List<Component> {
        componentInfos?.let { list ->
            return list.asSequence()
                .map {
                    Component().apply {
                        this.name = it.name
                        this.type = type
                    }
                }
                .toList()
        }
        return emptyList()
    }

    override suspend fun getReceiversActions(pkg: String): Map<String, Array<String>> {
        return packageManager.queryBroadcastReceivers(
            Intent().setPackage(pkg), PackageManager.GET_RESOLVED_FILTER
        )
            .asSequence()
            .filter { it.filter.countActions() > 0 }
            .map {
                it.activityInfo.name to Array(it.filter.countActions()) { idx ->
                    it.filter.getAction(
                        idx
                    )
                }
            }
            .toMap()
    }

    override suspend fun getDisabledComponentNames(pkg: String): Set<String> {
        return HashSet<String>().apply {
            val file = localRulesFile(pkg)
            if (!file.exists()) {
                if (isRulesApplied(pkg)) {
                    // If there is an existing rules file, then copy it to local data path
                    Shell.SU.run("cp $IFW_DATA$pkg.xml $localIfwRulesPath")
                } else return@apply
            }
            val check = "<component-filter name=\"${pkg}/"
            file.forEachLine { line ->
                val begin = line.indexOf(check).plus(check.length)
                val end = if (begin < check.length) -1 else line.indexOf("\"", begin)
                if (end >= 0) {
                    add(line.substring(begin, end))
                }
            }
        }
    }

    override suspend fun isRulesApplied(pkg: String): Boolean =
        Shell.SU.run("test -e '${IFW_DATA}${pkg}.xml' && echo 1 || echo 0").getStdout() == "1"


    override suspend fun applyRules(pkg: String, apply: Boolean) {
        if (!apply || !localRulesFile(pkg).exists()) {
            if (isRulesApplied(pkg))
                Shell.SU.run("rm -rf $IFW_DATA$pkg.xml && am force-stop $pkg")
            return
        }
        Shell.SU.run("cp ${localRulesFile(pkg).absolutePath} $IFW_DATA && chmod 0666 $IFW_DATA$pkg.xml && am force-stop $pkg")
    }

    override suspend fun saveDisabledComponents(pkg: String, disabledComponents: List<Component>) {
        if (disabledComponents.isEmpty()) {
            localRulesFile(pkg).apply { if (exists()) delete() }
            return
        }
        val activities = StringBuilder()
        val services = StringBuilder()
        val receivers = StringBuilder()
        disabledComponents.forEach {
            when (it.type) {
                ComponentType.ACTIVITY -> activities
                ComponentType.RECEIVER -> receivers
                ComponentType.SERVICE -> services
                else -> null
            }?.append("<component-filter name=\"$pkg/${it.name}\"/>\n")
        }
        val rules = "<rules>\n" +
                (if (activities.isEmpty()) "" else "<activity block=\"true\" log=\"false\">\n${activities}</activity>\n") +
                (if (services.isEmpty()) "" else "<service block=\"true\" log=\"false\">\n${services}</service>\n") +
                (if (receivers.isEmpty()) "" else "<broadcast block=\"true\" log=\"false\">\n${receivers}</broadcast>\n") +
                "</rules>"
        localRulesFile(pkg).writeText(rules)
    }

    private fun localRulesFile(pkg: String): File = File(localIfwRulesPath, "${pkg}.xml")

}