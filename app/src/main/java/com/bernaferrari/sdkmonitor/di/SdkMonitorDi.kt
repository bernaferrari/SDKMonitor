package com.bernaferrari.sdkmonitor.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.data.source.local.createAppDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@KoinApplication(modules = [SdkMonitorModule::class])
object SdkMonitorKoinApp

@Module
@ComponentScan("com.bernaferrari.sdkmonitor")
class SdkMonitorModule

@Single
fun workerPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences("workerPreferences", Context.MODE_PRIVATE)

@Single
fun settingsDataStore(context: Context): DataStore<Preferences> = context.settingsDataStore

// Room 3 AppDatabase / DAOs come from :shared commonMain; only the builder is Android-specific.
@Single
fun appDatabase(context: Context): AppDatabase = createAppDatabase(context)

@Single
fun appsDao(database: AppDatabase): AppsDao = database.snapsDao()

@Single
fun versionsDao(database: AppDatabase): VersionsDao = database.versionsDao()
