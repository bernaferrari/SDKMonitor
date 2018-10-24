package com.bernaferrari.sdkmonitor.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.core.MvRxEpoxyController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancel
import kotlin.coroutines.experimental.CoroutineContext

abstract class BaseMainFragment : BaseMvRxFragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    val disposableManager = CompositeDisposable()

    val epoxyController by lazy { epoxyController() }

    abstract fun epoxyController(): MvRxEpoxyController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.setController(epoxyController)
    }

    override fun invalidate() {
        recycler.requestModelBuild()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        disposableManager.clear()
        super.onDestroyView()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }
}
