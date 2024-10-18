package com.bernaferrari.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bernaferrari.base.misc.normalizeString
import com.bernaferrari.base.misc.onTextChanged
import com.bernaferrari.base.misc.showKeyboardOnView
import com.bernaferrari.base.view.onScroll
import com.bernaferrari.ui.base.SharedBaseFrag
import com.bernaferrari.ui.databinding.FragSearchBinding
import com.bernaferrari.ui.extensions.hideKeyboardWhenNecessary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/**
 * Simple fragment with a search box, a toolbar and a recyclerview.
 */
abstract class BaseSearchFragment : SharedBaseFrag(), CoroutineScope {

    lateinit var viewContainer: FrameLayout

    open val showKeyboardWhenLoaded = true

    open val sidePadding = 0

    private var _binding: FragSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        _binding.apply {
            recyclerView = binding.recycler
            viewContainer = binding.baseContainer
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
                        or WindowInsetsCompat.Type.displayCutout()
            )
            v.updatePadding(
                left = bars.left,
                top = bars.top,
                right = bars.right,
                //bottom = bars.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // app might crash if user is scrolling fast and quickly switching screens,
        // so the nullable seems necessary.
        recyclerView.onScroll { _, dy ->
            // this will take care of titleElevation
            // recycler might be null when back is pressed
            val raiseTitleBar = dy > 0 || recyclerView.computeVerticalScrollOffset() != 0
            binding.titleBar.isActivated = raiseTitleBar // animated via a StateListAnimator
        }

        recyclerView.updatePadding(left = sidePadding, right = sidePadding)

        binding.toolbarMenu.isVisible = showMenu

        if (showMenu) {
            (activity as? AppCompatActivity)?.setSupportActionBar(
                binding.toolbarMenu
            )
            binding.toolbarMenu.title = null
        }

        binding.queryInput.onTextChanged { search ->
            binding.queryClear.isInvisible = search.isEmpty()
            recyclerView.smoothScrollToPosition(0)
            onTextChanged(search.toString().normalizeString())
        }

        binding.searchIcon.setOnClickListener {
            binding.queryInput.showKeyboardOnView()
        }

        if (showKeyboardWhenLoaded) {
            binding.queryInput.showKeyboardOnView()
        }

        hideKeyboardWhenNecessary(recyclerView, binding.queryInput)

        binding.queryClear.setOnClickListener {
            binding.queryInput.setText("")
        }

        if (closeIconRes == null) {
            binding.close.visibility = View.GONE
        } else {
            val closeIcon = closeIconRes ?: 0
            binding.close.setImageResource(closeIcon)
            binding.close.setOnClickListener { dismiss() }
        }
    }

    abstract fun onTextChanged(searchText: String)

    fun setInputHint(hint: String) {
        _binding?.queryInput?.hint = hint
    }

    fun getInputText(): String = binding.queryInput.text.toString()

    fun scrollToPosition(pos: Int) = recyclerView.scrollToPosition(pos)

    override fun onDestroy() {
        coroutineContext.cancel()
        disposableManager.clear()
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
