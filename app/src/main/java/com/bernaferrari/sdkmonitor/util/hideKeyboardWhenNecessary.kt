package com.bernaferrari.sdkmonitor.util

import android.app.Activity
import android.view.KeyEvent
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bernaferrari.sdkmonitor.extensions.onEditorAction
import com.bernaferrari.sdkmonitor.extensions.onKey
import com.bernaferrari.sdkmonitor.extensions.onScroll

fun hideKeyboardWhenNecessary(
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

fun InputMethodManager.hideKeyboard(editText: EditText) {
    this.hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    if (editText.text.isEmpty()) {
        // loose the focus when scrolling and the text is empty, this way the
        // cursor will be hidden.
        editText.isFocusable = false
        editText.isFocusableInTouchMode = true
    }
}