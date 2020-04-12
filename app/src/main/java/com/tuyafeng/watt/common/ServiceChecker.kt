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

import com.jaredrummler.android.shell.Shell

object ServiceChecker {
    private val runningServices: MutableSet<String> = HashSet()
    private var pkg: String = ""

    suspend fun update(pkg: String = this.pkg) {
        this.pkg = pkg
        if (pkg.isEmpty()) {
            runningServices.clear()
            return
        }
        val serviceInfo = Shell.SU.run("dumpsys activity services -p $pkg").getStdout()
        if (serviceInfo.isEmpty() || serviceInfo.contains("(nothing)")) {
            runningServices.clear()
            return
        }
        var begin: Int = -1
        var end: Int = -1
        val check = "$pkg/"
        runningServices.addAll(serviceInfo.split("\n[\n]+".toRegex())
            .asSequence()
            .filter { it.contains("* ServiceRecord{") } // It is service info
            .filterNot { it.contains("app=null") } // Service is not running
            .filter {
                begin = it.indexOf(check) + check.length
                end = it.indexOf("}", begin)
                begin in 0 until end // Name is available
            }
            .map { (if (it[begin] == '.') pkg else "") + it.substring(begin, end) })
    }

    fun isRunning(name: String): Boolean = runningServices.contains(name)
}