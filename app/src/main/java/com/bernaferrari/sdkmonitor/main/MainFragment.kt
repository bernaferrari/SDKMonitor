package com.bernaferrari.sdkmonitor.main

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.*
import com.bernaferrari.sdkmonitor.ui.InsetDecoration
import com.bernaferrari.sdkmonitor.util.ViewModelFactory
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class AppVersion(
    val app: App,
    val sdkVersion: Int,
    val lastUpdateTime: String,
    val cornerRadius: Float
)

class MainFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main + Job()

    private lateinit var model: MainViewModel
    private val disposable = CompositeDisposable()

    private val itemDecorator by lazy {
        InsetDecoration(
            resources.getDimensionPixelSize(R.dimen.right_padding_for_fast_scroller),
            false,
            true
        )
    }

    private val controller = MainController()

    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.getSystemService<InputMethodManager>()
        } else {
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } ?: throw Exception("null activity. Can't bind inputMethodManager")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.main_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = viewModelProvider(ViewModelFactory.getInstance())

        if (model.itemsList.isEmpty()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        } else {
            progressBar.isVisible = false
        }

        if (recycler.adapter == null) {
            val linearLayoutManager = LinearLayoutManager(context).apply {
                initialPrefetchItemCount = 8
            }

            recycler.setHasFixedSize(true)
            recycler.layoutManager = linearLayoutManager
            recycler.adapter = controller.adapter
            recycler.addItemDecoration(itemDecorator)

            setupFastScroller(linearLayoutManager)
        }

        filter.setOnClickListener {
            //            MaterialDialog(requireContext())
        }

        controller.onClick = { model, v ->

            val extras =
                FragmentNavigatorExtras(v.findViewWithTag<View>(model.app.packageName) to "shared")

            val bundle = bundleOf("packageName" to model.app.packageName)

            disposable.clear()
            v.findNavController().navigate(
                R.id.action_mainFragment_to_detailsFragment,
                bundle,
                null,
                extras
            )

        }

        var work: Job? = null
        val cornerRadius = 8.toDpF(resources)

        disposable += model.appsList
            .takeWhile { model.allItems.size != it.size }
            .doOnNext { if (it.isEmpty()) model.updateAll() }
            .debounce { list ->
                // debounce with a 200ms delay all items except the first one
                val flow = Flowable.just(list)

                flow.takeIf { model.allItems.isEmpty() && list.isEmpty() }
                    ?.let { it } ?: flow.delay(200, TimeUnit.MILLISECONDS)
            }
            .map { list ->
                list.also { model.allItems.clear() }
                    .asSequence()
                    .sortedBy { it.title.toLowerCase() }
                    .mapTo(model.allItems) { app ->
                        val (sdkVersion, lastUpdate) = model.getSdkDate(app)
                        AppVersion(app, sdkVersion, lastUpdate, cornerRadius)
                    }
            }
            .doOnNext {
                model.itemsList.clear()
                model.itemsList.addAll(model.allItems)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.isVisible = model.allItems.isEmpty()

                if (model.allItems.isNotEmpty()) {
                    queryInput.hint = "Search ${model.allItems.size} apps.."
                }

                // if work is active, it means user is searching for something, so the
                // other disposable will be called.
                if (work?.isActive != true) {
                    controller.setData(model.allItems)
                }
            }

        queryInput.onTextChanged { txt ->
            val input = txt.toString()
            queryClear.isVisible = input.isNotEmpty()

            work?.cancel()
            work = launch {
                // If the user types anything before data has loaded, this will
                // delay and try again until it is available or the user types
                // another thing.
                // Without this, the input would be ignored while data is loading.
                while (model.allItems.isEmpty()) delay(200)

                val newList = withContext(Dispatchers.IO) {
                    val list = model.allItems.toList().takeIf { it.isNotEmpty() }
                        ?.filter {
                            it.app.title.normalizeString().contains(input.normalizeString())
                        } ?: model.allItems

                    list.also {
                        model.itemsList.clear()
                        model.itemsList.addAll(it)
                    }
                }
                controller.setData(newList)
            }
        }

        queryClear.setOnClickListener { queryInput.setText("") }

        setupHideKeyboardWhenNecessary(
            requireActivity(),
            inputMethodManager,
            recycler,
            queryInput
        )

    }

    override fun onDestroy() {
        println("onDestroy!!")
        disposable.clear()
        coroutineContext.cancel()
        super.onDestroy()
    }

    private fun setupFastScroller(linearLayoutManager: LinearLayoutManager) {
        fastscroller.setupWithRecyclerView(
            recyclerView = recycler,
            useDefaultScroller = false,
            getItemIndicator = {
                // or fetch the section at [position] from your database
                val letter = model.itemsList[it].app.title.substring(0, 1)
                val index = if (letter[0].isDigit()) "#" else letter.toUpperCase()

                FastScrollItemIndicator.Text(index) // Return a text indicator
            }
        )

        val smoothScroller: LinearSmoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START
        }

        fastscroller.itemIndicatorSelectedCallbacks += object :
            FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(
                indicator: FastScrollItemIndicator,
                indicatorCenterY: Int,
                itemPosition: Int
            ) {
                recycler.stopScroll()
                inputMethodManager.hideKeyboard(queryInput)
                smoothScroller.targetPosition = itemPosition
                linearLayoutManager.startSmoothScroll(smoothScroller)
            }
        }

        fastscroller_thumb.setupWithFastScroller(fastscroller)
    }

    companion object {
        private fun setupHideKeyboardWhenNecessary(
            activity: Activity,
            inputMethodManager: InputMethodManager,
            recyclerView: RecyclerView,
            editText: EditText
        ) {
            // hide keyboard when user scrolls
            val touchSlop = ViewConfiguration.get(activity).scaledTouchSlop
            var totalDy = 0

            recyclerView.onScroll { _, dy ->
                if (dy > 0) {
                    totalDy += dy
                    if (totalDy >= touchSlop) {
                        totalDy = 0
                        inputMethodManager.hideKeyboard(editText)
                    }
                }
            }

            // hide keyboard when user taps enter
            editText.onKey {
                if (it.keyCode == KeyEvent.KEYCODE_ENTER) {
                    inputMethodManager.hideKeyboard(editText)
                    true
                } else {
                    false
                }
            }

            // hide keyboard when user taps to go
            editText.onEditorAction {
                if (it == EditorInfo.IME_ACTION_GO) {
                    inputMethodManager.hideKeyboard(editText)
                    true
                } else {
                    false
                }
            }
        }

        private fun InputMethodManager.hideKeyboard(editText: EditText) {
            this.hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            if (editText.text.isEmpty()) {
                // loose the focus when scrolling and the text is empty, this way the
                // cursor will be hidden.
                editText.isFocusable = false
                editText.isFocusableInTouchMode = true
            }
        }
    }
}
