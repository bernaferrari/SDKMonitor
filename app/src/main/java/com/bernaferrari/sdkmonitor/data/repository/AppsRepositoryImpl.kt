package com.bernaferrari.sdkmonitor.data.repository

import android.content.Context
import com.bernaferrari.sdkmonitor.data.source.local.AppsDao
import com.bernaferrari.sdkmonitor.data.source.local.VersionsDao
import com.bernaferrari.sdkmonitor.domain.AppVersion
import com.bernaferrari.sdkmonitor.domain.LogEntry
import com.bernaferrari.sdkmonitor.domain.TrackedApp
import com.bernaferrari.sdkmonitor.domain.TrackedVersion
import com.bernaferrari.sdkmonitor.domain.repository.AppsRepository
import com.bernaferrari.sdkmonitor.extensions.convertTimestampToDate
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

/**
 * Android [AppsRepository] — thin shell over shared [RoomAppsRepository] (commonMain Room 3)
 * with locale-aware timestamp formatting.
 */
@Single(binds = [AppsRepository::class])
class AppsRepositoryImpl(
    appsDao: AppsDao,
    versionsDao: VersionsDao,
    context: Context,
) : AppsRepository by RoomAppsRepository(
        appsDao = appsDao,
        versionsDao = versionsDao,
        formatTimestamp = { ts -> ts.convertTimestampToDate(context) },
    )
