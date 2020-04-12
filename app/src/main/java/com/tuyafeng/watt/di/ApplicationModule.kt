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

package com.tuyafeng.watt.di

import android.content.Context
import android.content.pm.PackageManager
import com.tuyafeng.watt.data.apps.AppsDataSource
import com.tuyafeng.watt.data.apps.source.AppsLocalDataSource
import com.tuyafeng.watt.data.components.ComponentsDataSource
import com.tuyafeng.watt.data.components.source.ComponentsLocalDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class AppsLocalDataSource

    @Qualifier
    @Retention(RUNTIME)
    annotation class ComponentsLocalDataSource

    @JvmStatic
    @Singleton
    @AppsLocalDataSource
    @Provides
    fun provideAppsLocalDataSource(
        packageManager: PackageManager
    ): AppsDataSource {
        return AppsLocalDataSource(packageManager)
    }

    @JvmStatic
    @Singleton
    @ComponentsLocalDataSource
    @Provides
    fun provideComponentsLocalDataSource(
        localIfwRulesPath: String,
        packageManager: PackageManager
    ): ComponentsDataSource {
        return ComponentsLocalDataSource(localIfwRulesPath, packageManager)
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideLocalIfwRulesPath(context: Context): String {
        var file: File? = context.applicationContext.getExternalFilesDir("ifw")
        if (file == null || (!file.exists() && !file.mkdirs())) {
            file = File(context.applicationContext.filesDir, "ifw")
            if (!file.exists() && !file.mkdirs()) {
                throw FileNotFoundException("Can not get correct path to save ifw rules")
            }
        }
        return file.absolutePath
    }

    @JvmStatic
    @Singleton
    @Provides
    fun providePackageManager(context: Context): PackageManager =
        context.applicationContext.packageManager

    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

}

@Module
abstract class ApplicationModuleBinds {}
