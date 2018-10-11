package com.bernaferrari.sdkmonitor

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.ChangeDatabase
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import dagger.Component
import dagger.Module
import dagger.Provides
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
}

@Module
class SnapsRepositoryModule {

    @Singleton
    @Provides
    internal fun provideAppsDao(db: ChangeDatabase): AppsDao = db.snapsDao()

    @Singleton
    @Provides
    internal fun provideVersionsDao(db: ChangeDatabase): VersionsDao = db.versionsDao()
}

@Module
class RepositoriesMutualDependenciesModule {

    @Singleton
    @Provides
    internal fun provideDb(context: Context): ChangeDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ChangeDatabase::class.java,
            "Changes.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}

@Component(modules = [ContextModule::class, AppModule::class, SnapsRepositoryModule::class, RepositoriesMutualDependenciesModule::class])
@Singleton
interface SingletonComponent {
    fun appContext(): Context
    fun sharedPrefs(): SharedPreferences
    fun appsDao(): AppsDao
    fun versionsDao(): VersionsDao
}

class Injector private constructor() {
    companion object {
        fun get(): SingletonComponent = MainApplication.get().component
    }
}
