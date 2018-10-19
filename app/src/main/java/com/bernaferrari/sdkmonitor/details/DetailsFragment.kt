package com.bernaferrari.sdkmonitor.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.ChangeBounds
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.extensions.viewModelProvider
import com.bernaferrari.sdkmonitor.main.AppVersion
import com.bernaferrari.sdkmonitor.main.MainController
import com.bernaferrari.sdkmonitor.util.ViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.cancel
import kotlin.coroutines.experimental.CoroutineContext

class DetailsFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var model: DetailsViewModel
    private val disposable = CompositeDisposable()
    private val controller = MainController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = ChangeBounds().apply { duration = 175 }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.details_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = viewModelProvider(ViewModelFactory.getInstance())

        val allItems = mutableListOf<AppVersion>()

        val linearLayoutManager = LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 8
        }

        recycler.layoutManager = linearLayoutManager
        recycler.adapter = controller.adapter

    }

    override fun onDestroy() {
        disposable.clear()
        coroutineContext.cancel()
        super.onDestroy()
    }

}
