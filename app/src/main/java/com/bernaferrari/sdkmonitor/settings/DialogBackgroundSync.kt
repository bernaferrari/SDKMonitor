package com.bernaferrari.sdkmonitor.settings

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bernaferrari.base.misc.onTextChanged
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.WorkerHelper
import kotlinx.android.synthetic.main.dialog_sync.view.*

// inspired from mnml
class DialogBackgroundSync : DialogFragment() {

    companion object {
        private const val TAG = "[ABOUT_DIALOG]"

        /** Shows the about dialog inside of [activity]. */
        fun show(activity: FragmentActivity) {
            val dialog = DialogBackgroundSync()
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: throw IllegalStateException("Oh no!")

        return MaterialDialog(context)
            .customView(R.layout.dialog_sync, noVerticalPadding = true)
            .also { it.getCustomView().setUpViews() }
    }

    private val singular by lazy { resources.getStringArray(R.array.singularTime) }
    private val plural by lazy { resources.getStringArray(R.array.pluralTime) }

    fun String.toEditText(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun View.setUpViews() {

        item_switch.isChecked = Injector.get().backgroundSync().get()

        Injector.get().syncInterval().get().also { prefs ->

            when (prefs.substring(0, 1).toInt()) {
                0 -> minutes.isChecked = true
                1 -> hours.isChecked = true
                2 -> days.isChecked = true
            }

            input.text = prefs.substring(1, 3).toEditText()
        }

        // update all descriptions and visibilities.
        fixSingularPlural()
        updateDescription()
        updatePickerVisibility()

        // add listeners
        input.onTextChanged {
            fixSingularPlural()
            updateDescription()
            updateSyncInterval()
        }

        title_bar.setOnClickListener {
            item_switch.toggle()
            updatePickerVisibility()
            // set shared value for backgroundSync
            Injector.get().backgroundSync().set(item_switch.isChecked)
        }
    }

    private fun View.updatePickerVisibility() {
        intervalGroup.isVisible = item_switch.isChecked
        input.isVisible = item_switch.isChecked
    }

    private fun View.fixSingularPlural() {
        val newVal = input.text.toString().let { if (it.isEmpty()) "0" else it }.toInt()

        if (newVal == 1) {
            minutes.text = singular[0]
            hours.text = singular[1]
            days.text = singular[2]
        } else {
            minutes.text = plural[0]
            hours.text = plural[1]
            days.text = plural[2]
        }
    }

    //
    private fun View.updateSyncInterval() {
        // set shared value for syncInterval
        val prefs = Injector.get().syncInterval()
//        val firstDigit = kindPicker.value
//        val secondThirdDigits = "%02d".format(numberPicker.value)
//        prefs.set("$firstDigit$secondThirdDigits")

        // update workManager
        WorkerHelper.updateBackgroundWorker(true)
    }

    private fun View.whichOneIsSelected(): String {
        return when {
            minutes.isChecked -> getString(R.string.min)
            hours.isChecked -> getString(R.string.hours)
            days.isChecked -> getString(R.string.days)
            else -> ""
        }
    }

    private fun View.updateDescription() {
        // update text string
        if (!item_switch.isChecked) {
            nextSync.text = getString(R.string.sync_disabled)
        } else {
            val parseInput = input.text.toString()
                .let { if (it.isEmpty()) "0" else it }.toInt()
                .let { if (it < 15) 15 else it }

            // retrieve correct word pronunciation before writing on screen
            nextSync.text = getString(R.string.sync_every, parseInput, whichOneIsSelected())
        }
    }
}
