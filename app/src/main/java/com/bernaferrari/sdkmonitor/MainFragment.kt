package com.bernaferrari.sdkmonitor

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.systemService
import androidx.core.view.isVisible
import com.bernaferrari.sdkmonitor.extensions.*
import com.jakewharton.rxbinding2.widget.RxTextView
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import java.util.concurrent.TimeUnit


class MainFragment : Fragment() {

    private lateinit var model: TextViewModel
    private val disposable = CompositeDisposable()
    private val itemsList = mutableListOf<RowItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = viewModelProvider(ViewModelFactory.getInstance())

        val allItems = mutableListOf<RowItem>()
        val section = Section(itemsList)
        val groupAdapter = GroupAdapter<ViewHolder>().apply { add(section) }

        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context).apply { initialPrefetchItemCount = 8 }
        recycler.adapter = groupAdapter
        fastscroller_thumb.setupWithFastScroller(fastscroller)

        // val aaa = AppManager.getPackageInfo(list.first().packageName)

        disposable += model.concertList
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
                    .mapTo(allItems) { snap -> RowItem(snap) }
                    .sortBy { it.snap.title.toLowerCase() }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                progressBar.isVisible = allItems.isEmpty()
                section.update(allItems)
                itemsList.clear()
                itemsList.addAll(allItems)
            }

        disposable += RxTextView.textChanges(query)
            .doOnNext { clear_query.isVisible = it.isNotEmpty() }
            .debounce(200, TimeUnit.MILLISECONDS)
            .map { input ->
                allItems.takeIf { it.isNotEmpty() }
                    ?.filter {
                        it.snap.title.normalizeString().contains(input.toString().normalizeString())
                    } ?: allItems
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                section.update(it)
                itemsList.clear()
                itemsList.addAll(it)
            }

        clear_query.setOnClickListener { query.setText("") }

        setupHideKeyboardWhenNecessary(requireActivity(), recycler, query)

        setupFastScroller()
    }

    override fun onDestroy() {
        disposable.clear()
        super.onDestroy()
    }

    private fun setupFastScroller() {
        fastscroller.setupWithRecyclerView(recycler, {
            // or fetch the section at [position] from your database
            FastScrollItemIndicator.Text(
                // Grab the first letter and capitalize it
                itemsList[it].snap.title.substring(0, 1).toUpperCase()
            ) // Return a text indicator
        }
        )
    }

    companion object {
        private fun setupHideKeyboardWhenNecessary(
            activity: Activity,
            recyclerView: RecyclerView,
            editText: EditText
        ) {
            val inputMethodManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.systemService<InputMethodManager>()
            } else {
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            } ?: return

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
        }
    }

}
