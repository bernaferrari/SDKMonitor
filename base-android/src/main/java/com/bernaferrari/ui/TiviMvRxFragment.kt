package com.bernaferrari.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MvRxView
import com.airbnb.mvrx.MvRxViewModelStore
import dagger.android.support.DaggerFragment

abstract class TiviMvRxFragment : DaggerFragment(), MvRxView {

    abstract val recyclerView: EpoxyRecyclerView

    private val epoxyController by lazy { epoxyController() }

    override val mvrxViewModelStore by lazy { MvRxViewModelStore(viewModelStore) }

    abstract fun epoxyController(): EpoxyController

    open fun layoutManager(): RecyclerView.LayoutManager = LinearLayoutManager(context)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.layoutManager = layoutManager()
        recyclerView.setController(epoxyController)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvrxViewModelStore.restoreViewModels(this, savedInstanceState)
        epoxyController.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mvrxViewModelStore.saveViewModels(outState)
        epoxyController.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }

    override fun onDestroyView() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroyView()
    }

    override fun invalidate() {
        recyclerView.requestModelBuild()
    }

    fun getModelAtPos(pos: Int): EpoxyModel<*> {
        return epoxyController.adapter.getModelAtPosition(pos)
    }
}
