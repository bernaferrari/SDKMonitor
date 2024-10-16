package com.bernaferrari.ui.standard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bernaferrari.ui.R
import com.bernaferrari.ui.databinding.FragElasticStandardBinding
import com.bernaferrari.ui.widgets.ElasticDragDismissFrameLayout

/**
 * BaseToolbarFragment with a Elastic behavior (user can scroll beyond top/bottom to dismiss it).
 */
abstract class BaseElasticToolbarFragment : BaseToolbarFragment() {

    private var _binding: FragElasticStandardBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragElasticStandardBinding.inflate(inflater, container, false)
        val view = binding.root
        _binding.apply {
            recyclerView = view.findViewById(R.id.recycler)
            viewContainer = view.findViewById(R.id.baseContainer)
            toolbar = view.findViewById(R.id.toolbarMenu)
            titleBar = view.findViewById(R.id.title_bar)
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

        val chromeFader =
            ElasticDragDismissFrameLayout.SystemChromeFader(activity as AppCompatActivity)
        binding.elasticContainer.addListener(chromeFader)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
