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
import com.bernaferrari.sdkmonitor.details.DetailsDialog
import com.bernaferrari.sdkmonitor.logs.LogsFragment
import com.bernaferrari.sdkmonitor.main.MainFragment
import com.bernaferrari.sdkmonitor.settings.SettingsFragment
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
abstract class AppModule

@Module
class AppContextModule {

    @Provides
    fun provideContext(application: MainApplication): Context = application.applicationContext

    @Provides
    fun sharedPrefs(application: MainApplication): SharedPreferences {
        return application.getSharedPreferences("workerPreferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun rxPrefs(application: MainApplication): RxkPrefs {
        return rxkPrefs(sharedPrefs(application))
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


@Module
abstract class SdkInjectorsModule {

    @ContributesAndroidInjector
    abstract fun mainFragment(): MainFragment

    @ContributesAndroidInjector
    abstract fun detailsDialog(): DetailsDialog

    @ContributesAndroidInjector
    abstract fun logsFragment(): LogsFragment

    @ContributesAndroidInjector
    abstract fun settingsFragment(): SettingsFragment

}


@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        AppContextModule::class,
        AppModule::class,
        RxPrefsModule::class,
        SnapsRepositoryModule::class,
        RepositoriesMutualDependenciesModule::class,
        SdkInjectorsModule::class]
)
@Singleton
interface SingletonComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: MainApplication): Builder

        fun build(): SingletonComponent
    }

    fun inject(app: MainApplication)

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
