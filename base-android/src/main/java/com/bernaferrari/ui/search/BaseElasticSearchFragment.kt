package com.bernaferrari.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.bernaferrari.ui.R
import com.bernaferrari.ui.databinding.FragElasticSearchBinding
import com.bernaferrari.ui.widgets.ElasticDragDismissFrameLayout

/**
 * SearchFragment with a Elastic behavior (user can scroll beyond top/bottom to dismiss it).
 */
abstract class BaseElasticSearchFragment : BaseSearchFragment() {

    private var _binding: FragElasticSearchBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragElasticSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.apply {
            recyclerView = view.findViewById(R.id.recycler)
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
