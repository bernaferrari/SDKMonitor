<p align="center"><img src="assets/logo.png" alt="ChangeDetection" height="200px"></p>

SDK Monitor
=================

This app tracks changes on targetSDK from your apps. Starting November 1st, 2018, Google is [requiring all app updates](https://developer.android.com/distribute/best-practices/develop/target-sdk) to target at least 26 (28 is the latest).

The idea behind this project was to make it easy to see the apps which are "voluntarily" being updated regularly, and the ones that are resisting until the last second.
I am personally a fan of [App Inspector](https://play.google.com/store/apps/details?id=bg.projectoria.appinspector), with 100K+ downloads, simple interface, and great information. I had, however, 3 issues with it:

* Really long time to load when app is opened;
* No search or way to find what you want;
* No material design.

Based on this, I made an improved app, with everything App Inspector has and more. SDK Monitor caches everything using Room, so time to load is **REALLY** fast.
It also makes use of Implicit Broadcasts (where available) to automatically keep track of app installs, updates and deletions.
Android Oreo removed these (except deletion), so the app has the option to use WorkManager to automatically fetch periodically in background for these changes.
Every time the targetSDK value for an app is changed, the app will show a push notification.

This app also showcases the following Jetpack libraries working together: [Room](https://developer.android.com/topic/libraries/architecture/room.html), [ViewModel](https://developer.android.com/reference/android/arch/lifecycle/ViewModel.html), [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager), [DataBinding](https://developer.android.com/topic/libraries/data-binding/) and [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/).

App is currently in beta, you can download the apk here:

[APK DOWNLOAD](https://github.com/bernaferrari/SDKMonitor/raw/master/assets/sdkmonitor.apk)

## Screenshots

| Fast Scroller | Search | Details | Settings |
|:-:|:-:|:-:|:-:|
| ![First](assets/scroller.jpg?raw=true) | ![Sec](assets/search.jpg?raw=true) | ![Third](assets/details.jpg?raw=true) | ![Fourth](assets/settings.jpg?raw=true) |

Introduction
------------

### Features

This app contains the following screens:
* A list of all installed apps that were downloaded from Play Store.
* A settings view, that allows user to toggle auto-sync on/off and configure what is required for a sync to occur.
* \[Eventually\] The plan is to have a filter screen (sort by name/last update/targetSDK value) and an about screen.

#### Presentation layer

This app is a Single-Activity app, with the following components:
* A main activity that handles navigation.
* A fragment to display the list of apps currently tracked. This fragment makes use of [MvRx](https://github.com/airbnb/MvRx) architecture, and it was a delight integrating search with database on the viewmodel.

The app uses a Model-View-ViewModel (MVVM) architecture for the presentation layer. Each of the fragments corresponds to a MVVM View.
The View and ViewModel communicate using RxJava2 and general good principles.

#### Data layer

The database is created using Room and it has two entities: a `App` and a `Version` that generate corresponding SQLite tables at runtime.
There is a one to many relationship between them. The packageName from `App` is a foreign key on `Version`.
App contains the app label, package name and color (to be displayed on the app, based on the icon).
Version contains the targetSDK version, versionName and versionCode. The app only adds a new version when the targetSDK changes.

To let other components know when the data has finished populating, the `ViewModel` exposes a `Flowable` object.
The app also makes use of Kotlin's Coroutines to deal with some callbacks.

#### How components were used

* MvRx and Epoxy: used on the main screen to fetch and filter (if necessary) the list of apps. Since Epoxy wasn't made for items that are changing, the Settings view makes use of Groupie.

* ViewModel: A *Observables.combineLatest* will merge the results from database (which will be fetched if empty) and search (which will be empty when app is first opened). Following this, the *execute* from MvRx will copy the state to the correct EpoxyController.

* WorkManager: responsible for automatically syncing when the app is in background.
There are two constraints: *battery not low* and *device charging*.

#### Third Party Libraries Used

  * [Android-Iconics][1] deal with icons without suffering.
  * [Architecture Components][2] stated above.
  * [Epoxy][3] for making static RecyclerViews as efficient and nice as possible.
  * [Groupie][4] for making RecyclerViews as simple as possible. Unfortunately it is kind of deprecated, so a lot of work was done using Epoxy.
  * [Coroutines][5] for simple background work.
  * [Logger][6] logs that are useful and can disabled on release.
  * [material-about-library][7] \[eventually\]create an about page without suffering.
  * [Material Dialogs][8] show dialogs in a simple and easy way.
  * [Notify][9] create notifications without effort.
  * [MvRx][10] on the main fragment.
  * [Stetho][11] debug the database easily.
  * [timeago][12] makes it easy display relative dates (i.e. 1 day ago).
  * [RxJava][13] deals with MvRx and coordinates most of the work on the app.
  * [Dagger 2][14] dependency injection for sharedPreferences with application Context, provides singleton database instances.

#### Special thanks
A lot of the structure and ideas from this app came from [Changes](https://play.google.com/store/apps/details?id=com.saladevs.changelogclone), which is [also open source](https://github.com/GSala/Changelogs).


[1]: https://github.com/mikepenz/Android-Iconics
[2]: https://developer.android.com/topic/libraries/architecture/
[3]: https://github.com/airbnb/epoxy
[4]: https://github.com/lisawray/groupie
[5]: https://github.com/Kotlin/kotlinx.coroutines
[6]: https://github.com/orhanobut/logger
[7]: https://github.com/daniel-stoneuk/material-about-library
[8]: https://github.com/afollestad/material-dialogs
[9]: https://github.com/Karn/notify
[10]: https://github.com/airbnb/MvRx
[11]: http://facebook.github.io/stetho/
[12]: https://github.com/marlonlom/timeago
[13]: https://github.com/ReactiveX/RxJava
[14]: https://github.com/google/dagger


### Reporting Issues

Issues and Pull Requests are welcome.
You can report [here](https://github.com/bernaferrari/SDKMonitor/issues).

License
-------

Copyright 2018 Bernardo Ferrari.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
