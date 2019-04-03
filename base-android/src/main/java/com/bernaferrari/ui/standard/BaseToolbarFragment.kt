package com.bernaferrari.ui.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.epoxy.EpoxyRecyclerView
import com.bernaferrari.base.view.onScroll
import com.bernaferrari.ui.R
import com.bernaferrari.ui.base.SharedBaseFrag
import kotlinx.android.synthetic.main.frag_standard.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Simple fragment with a toolbar and a recyclerview.
 */
abstract class BaseToolbarFragment : SharedBaseFrag(), CoroutineScope {

    override val recyclerView: EpoxyRecyclerView by lazy { recycler }

    abstract val menuTitle: String?

    open val isMenuEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.frag_standard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isMenuEnabled) {
            (activity as? AppCompatActivity)?.setSupportActionBar(toolbarMenu)
        }

        toolbarMenu.title = menuTitle

        recycler?.onScroll { _, dy ->
            // this will take care of titleElevation
            // recycler might be null when back is pressed
            val raiseTitleBar = dy > 0 || recycler.computeVerticalScrollOffset() != 0
            title_bar?.isActivated = raiseTitleBar // animated via a StateListAnimator
        }

        if (closeIconRes == null) {
            close.visibility = View.GONE
        } else {
            val closeIcon = closeIconRes ?: 0
            close.setImageResource(closeIcon)
            close.setOnClickListener { dismiss() }
        }
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }
}
