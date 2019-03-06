package com.bernaferrari.sdkmonitor

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.afollestad.rxkprefs.Pref
import com.afollestad.rxkprefs.RxkPrefs
import com.afollestad.rxkprefs.rxkPrefs
import com.bernaferrari.sdkmonitor.data.source.local.AppDatabase
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class ContextModule(private val appContext: Context) {

    @Provides
    fun appContext(): Context = appContext
}

@Module
class AppModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun sharedPrefs(): SharedPreferences {
        return appContext.getSharedPreferences("workerPreferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun rxPrefs(): RxkPrefs {
        return rxkPrefs(sharedPrefs())
    }
}


@Module
class RxPrefsModule {

    @Provides
    @Named("lightMode")
    fun isLightTheme(rxPrefs: RxkPrefs): Pref<Boolean> {
        return rxPrefs.boolean("lightMode", true)
    }

    @Provides
    @Named("colorBySdk")
    fun isColorBySdk(rxPrefs: RxkPrefs): Pref<Boolean> {
        return rxPrefs.boolean("colorBySdk", true)
    }

    @Provides
    @Named("showSystemApps")
    fun showSystemApps(rxPrefs: RxkPrefs): Pref<Boolean> {
        return rxPrefs.boolean("showSystemApps", false)
    }

    @Provides
    @Named("backgroundSync")
    fun backgroundSync(rxPrefs: RxkPrefs): Pref<Boolean> {
        return rxPrefs.boolean("backgroundSync", false)
    }

    @Provides
    @Named("syncInterval")
    fun syncInterval(rxPrefs: RxkPrefs): Pref<String> {
        return rxPrefs.string("syncInterval", "301")
    }

    @Provides
    @Named("orderBySdk")
    fun orderBySdk(rxPrefs: RxkPrefs): Pref<Boolean> {
        return rxPrefs.boolean("orderBySdk", false)
    }
}


@Module
class SnapsRepositoryModule {

    @Singleton
    @Provides
    internal fun provideAppsDao(db: AppDatabase): AppsDao = db.snapsDao()

    @Singleton
    @Provides
    internal fun provideVersionsDao(db: AppDatabase): VersionsDao = db.versionsDao()
}

@Module
class RepositoriesMutualDependenciesModule {

    @Singleton
    @Provides
    internal fun provideDb(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Apps.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}


@Component(modules = [ContextModule::class, AppModule::class, RxPrefsModule::class, SnapsRepositoryModule::class, RepositoriesMutualDependenciesModule::class])
@Singleton
interface SingletonComponent {
    fun appContext(): Context
    fun sharedPrefs(): SharedPreferences
    fun appsDao(): AppsDao
    fun versionsDao(): VersionsDao

    @Named("lightMode")
    fun isLightTheme(): Pref<Boolean>

    @Named("colorBySdk")
    fun isColorBySdk(): Pref<Boolean>

    @Named("showSystemApps")
    fun showSystemApps(): Pref<Boolean>

    @Named("backgroundSync")
    fun backgroundSync(): Pref<Boolean>

    @Named("syncInterval")
    fun syncInterval(): Pref<String>

    @Named("orderBySdk")
    fun orderBySdk(): Pref<Boolean>
}

class Injector private constructor() {
    companion object {
        fun get(): SingletonComponent = MainApplication.get().component
    }
}
