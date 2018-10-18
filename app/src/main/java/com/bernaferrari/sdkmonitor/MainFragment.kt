package com.bernaferrari.sdkmonitor

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.*
import com.jakewharton.rxrelay2.BehaviorRelay
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
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

    private lateinit var model: TextViewModel
    private val disposable = CompositeDisposable()
    private val itemsList = mutableListOf<AppVersion>()
    private val itemDecorator by lazy {
        InsetDecoration(
            resources.getDimensionPixelSize(R.dimen.right_padding_for_fast_scroller),
            false,
            true
        )
    }

    private val controller = PhotoController()

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
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        query.requestFocus()
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        model = viewModelProvider(ViewModelFactory.getInstance())

        val allItems = mutableListOf<AppVersion>()

        val linearLayoutManager = LinearLayoutManager(context).apply {
            initialPrefetchItemCount = 8
        }

        recycler.setHasFixedSize(true)
        recycler.layoutManager = linearLayoutManager
        recycler.adapter = controller.adapter
        recycler.addItemDecoration(itemDecorator)

        filter.setOnClickListener {
            //            MaterialDialog(requireContext())
        }

        val relay = BehaviorRelay.create<String>()
        var work: Job? = null

        val cornerRadius = 8.toDpF(resources)

        disposable += model.appsList
            .doOnNext { if (it.isEmpty()) model.updateAll() }
            .debounce { list ->
                // debounce with a 200ms delay all items except the first one
                val flow = Flowable.just(list)
                flow
                    .takeIf { allItems.isEmpty() && list.isEmpty() }
                    ?.let { it } ?: flow.delay(200, TimeUnit.MILLISECONDS)
            }
            .map { list ->
                list.also { allItems.clear() }
                    .asSequence()
                    .sortedBy { it.title.toLowerCase() }
                    .mapTo(allItems) { app ->
                        val (sdkVersion, lastUpdate) = model.getSdkDate(app)
                        AppVersion(app, sdkVersion, lastUpdate, cornerRadius)
                    }
            }
            .doOnNext {
                itemsList.clear()
                itemsList.addAll(allItems)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                progressBar.isVisible = allItems.isEmpty()

                if (allItems.isNotEmpty()) query.hint = "Search ${allItems.size} apps.."

                // if work is active, it means user is searching for something, so the
                // other disposable will be called.
                if (work?.isActive != true) {
                    controller.setData(allItems)
                }
            }

        query.onTextChanged {
            clear_query.isVisible = it.isNotEmpty()
            work?.cancel()
            work = launch {
                // If the user types anything before data has loaded, this will
                // delay and try again until it is available or the user types
                // another thing.
                // Without this, the input would be ignored while data is loading.
                while (allItems.isEmpty()) delay(200)
                relay.accept(it.toString())
            }
        }

        disposable += relay
            .debounce(200, TimeUnit.MILLISECONDS)
            .map { input ->
                allItems.takeIf { it.isNotEmpty() }
                    ?.filter { it.app.title.normalizeString().contains(input.normalizeString()) }
                        ?: allItems
            }
            .doOnNext {
                itemsList.clear()
                itemsList.addAll(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { controller.setData(it) }

        clear_query.setOnClickListener { query.setText("") }

        setupHideKeyboardWhenNecessary(requireActivity(), inputMethodManager, recycler, query)

        setupFastScroller(linearLayoutManager)
    }

    override fun onDestroy() {
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
                val letter = itemsList[it].app.title.substring(0, 1)
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
                inputMethodManager.hideKeyboard(query)
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
