package com.bernaferrari.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.airbnb.epoxy.EpoxyRecyclerView
import com.bernaferrari.base.misc.normalizeString
import com.bernaferrari.base.misc.onTextChanged
import com.bernaferrari.base.misc.showKeyboardOnView
import com.bernaferrari.base.view.onScroll
import com.bernaferrari.ui.R
import com.bernaferrari.ui.TiviMvRxFragment
import com.bernaferrari.ui.extensions.hideKeyboardWhenNecessary
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.frag_search.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class BaseSearchFragment : TiviMvRxFragment(), CoroutineScope {

    override val recyclerView: EpoxyRecyclerView by lazy { recycler }

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    val container: ConstraintLayout by lazy { baseContainer }

    val disposableManager = CompositeDisposable()

    open val closeIconRes: Int? = 0

    open val showKeyboardWhenLoaded = true

    open val showMenu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(showMenu)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // app might crash if user is scrolling fast and quickly switching screens,
        // so the nullable seems necessary.
        recycler?.onScroll { _, dy ->
            // this will take care of titleElevation
            // recycler might be null when back is pressed
            val raiseTitleBar = dy > 0 || recycler.computeVerticalScrollOffset() != 0
            title_bar?.isActivated = raiseTitleBar // animated via a StateListAnimator
        }

        toolbarMenu.isVisible = showMenu

        if (showMenu) {
            (activity as? AppCompatActivity)?.setSupportActionBar(toolbarMenu)
            toolbarMenu.title = null
        }

        queryInput.onTextChanged { search ->
            queryClear.isInvisible = search.isEmpty()
            recycler.smoothScrollToPosition(0)
            onTextChanged(search.toString().normalizeString())
        }

        searchIcon.setOnClickListener {
            queryInput.showKeyboardOnView()
        }

        if (showKeyboardWhenLoaded) {
            queryInput.showKeyboardOnView()
        }

        hideKeyboardWhenNecessary(recycler, queryInput)

        queryClear.setOnClickListener { queryInput.setText("") }

        if (closeIconRes == null) {
            close.visibility = View.GONE
        } else {
            val closeIcon = closeIconRes ?: 0
            close.setImageResource(closeIcon)
            close.setOnClickListener { dismiss() }
        }
    }

    abstract fun onTextChanged(searchText: String)

    fun setInputHint(hint: String) {
        queryInput?.hint = hint
    }

    fun getInputText(): String = queryInput.text.toString()

    fun scrollToPosition(pos: Int) = recycler.scrollToPosition(pos)

    open fun dismiss() {
        activity?.onBackPressed()
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        disposableManager.clear()
        super.onDestroy()
    }
}
