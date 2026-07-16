package com.bernaferrari.sdkmonitor.data

/** Room [App] stores user apps as [App.isFromPlayStore]; system apps are the complement. */
val App.isSystemApp: Boolean get() = !isFromPlayStore
