package com.bernaferrari.sdkmonitor

import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.bernaferrari.sdkmonitor.settings.SettingsData
import com.bernaferrari.sdkmonitor.settings.SettingsState
import com.bernaferrari.sdkmonitor.settings.SettingsViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.rxkotlin.Observables
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

class SettingsViewModelTest {

//    private val dataSource: TasksDataSource = mock()

    private lateinit var viewModel: SettingsViewModel
    private lateinit var tasks: Observable<SettingsData>

    @Before
    fun setupTasksList() {
        tasks = source
    }

    @Test
    fun refreshTasks_success() {

        // given the viewmodel with default state
        viewModel = SettingsViewModel(SettingsState(), tasks)

        // verify that tasks were requested from the data source
//        verify(dataSource).getTasks()
        // verify that loading state has changed to true upon subscription
//        withState(viewModel) { assertEquals(it.isLoading, true) }

        // new emission from the data source happened
//        tasksSubject.onSuccess(tasks)

        // verify that tasks request was successful and the tasks list is present
//        withState(viewModel) {
//            assertTrue(it.taskRequest is Success)
//            assertEquals(it.tasks, tasks)
//            assertNull(it.lastEditedTask)
//        }

        // verify that loading state has changed after the stream is completed
        withState(viewModel) { assertEquals(it.data() != null, true) }
    }


    @Test
    fun rxTest() {

        val ob1 = TestObserver<Int>()
        val ob2 = TestObserver<Int>()

        val obs = BehaviorRelay.create<Int>()

        obs.accept(1)

        obs.subscribe(ob1)
        obs.subscribe(ob2)

        println("ob1: ${ob1.values()} || ob2: ${ob2.values()}")

        // unsubscribe
        ob1.dispose()
        ob2.dispose()
    }

    companion object {

        val settings = Observable.just(false)

        val source = Observables.combineLatest(
            settings,
            Observable.just(true),
            Observable.just(true),
            Observable.just(true),
            Observable.just(true)
        ) { dark, color, system, backgroundSync, orderBySdl ->
            SettingsData(dark, color, system, backgroundSync, orderBySdl)
        }

        @JvmField
        @ClassRule
        val mvrxTestRule = MvRxTestRule()
    }
}
