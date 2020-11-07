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
import com.tuyafeng.watt.data.PackagesDataSource
import com.tuyafeng.watt.data.PackagesLocalDataSource
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.annotation.AnnotationRetention.RUNTIME

@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {

    @Qualifier
    @Retention(RUNTIME)
    annotation class PackagesLocalDataSource

    @JvmStatic
    @Singleton
    @PackagesLocalDataSource
    @Provides
    fun providePackagesLocalDataSource(
        packageManager: PackageManager
    ): PackagesDataSource {
        return PackagesLocalDataSource(packageManager)
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
abstract class ApplicationModuleBinds
