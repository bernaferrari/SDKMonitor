package com.bernaferrari.ui.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.bernaferrari.base.view.onScroll
import com.bernaferrari.ui.base.SharedBaseFrag
import com.bernaferrari.ui.databinding.FragStandardBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Simple fragment with a toolbar and a recyclerview.
 */
abstract class BaseToolbarFragment : SharedBaseFrag(), CoroutineScope {

    abstract val menuTitle: String?

    lateinit var viewContainer: FrameLayout
    lateinit var toolbar: Toolbar
    lateinit var titleBar: ViewGroup

    private var _binding: FragStandardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragStandardBinding.inflate(inflater, container, false)
        val view = binding.root
        _binding.apply {
            recyclerView = binding.recycler
            viewContainer = binding.baseContainer
            toolbar = binding.toolbarMenu
            titleBar = binding.titleBar
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = menuTitle

        if (closeIconRes != 0) {
            toolbar.setNavigationIcon(closeIconRes ?: 0)
            toolbar.setNavigationOnClickListener { dismiss() }
        }

        recyclerView.onScroll { _, dy ->
            // this will take care of titleElevation
            // recycler might be null when back is pressed
            val raiseTitleBar = dy > 0 || recyclerView.computeVerticalScrollOffset() != 0
            binding.titleBar.isActivated = raiseTitleBar // animated via a StateListAnimator
        }
    }

    override fun onDestroy() {
        coroutineContext.cancel()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
