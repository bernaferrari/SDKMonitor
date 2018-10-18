package com.bernaferrari.sdkmonitor

import androidx.room.Embedded
import androidx.room.Relation
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.data.Version

class UserAndAllPets {
    @Embedded
    var user: App? = null
    @Relation(parentColumn = "packageName", entityColumn = "version")
    var pets: List<Version> = ArrayList()
}