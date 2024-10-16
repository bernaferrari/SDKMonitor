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
import com.bernaferrari.sdkmonitor.databinding.DialogSyncBinding

// inspired from mnml
class DialogBackgroundSync : DialogFragment() {
    private var _binding: DialogSyncBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: throw IllegalStateException("Oh no!")
        _binding = DialogSyncBinding.inflate(layoutInflater)

        return MaterialDialog(context)
            .customView(null, view = binding.root, noVerticalPadding = true)
            .also { it.getCustomView().setUpViews() }
    }

    private val singular by lazy { resources.getStringArray(R.array.singularTime) }
    private val plural by lazy { resources.getStringArray(R.array.pluralTime) }

    fun String.toEditText(): Editable = Editable.Factory.getInstance().newEditable(this)

    private fun View.setUpViews() {

        binding.itemSwitch.isChecked = Injector.get().backgroundSync().get()

        Injector.get().syncInterval().get().also { prefs ->

            when (prefs.substring(0, 1).toInt()) {
                0 -> binding.minutes.isChecked = true
                1 -> binding.hours.isChecked = true
                2 -> binding.days.isChecked = true
            }

            binding.input.text = prefs.substring(1, 3).toEditText()
        }

        // update all descriptions and visibilities.
        fixSingularPlural()
        updateDescription()
        updatePickerVisibility()

        // add listeners
        binding.input.onTextChanged {
            fixSingularPlural()
            updateDescription()
            updateSyncInterval()
        }

        binding.titleBar.setOnClickListener {
            binding.itemSwitch.toggle()
            updatePickerVisibility()
            // set shared value for backgroundSync
            Injector.get().backgroundSync().set(binding.itemSwitch.isChecked)
        }
    }

    private fun View.updatePickerVisibility() {
        binding.intervalGroup.isVisible = binding.itemSwitch.isChecked
        binding.input.isVisible = binding.itemSwitch.isChecked
    }

    private fun View.fixSingularPlural() {
        val newVal = binding.input.text.toString().let { it.ifEmpty { "0" } }.toInt()

        if (newVal == 1) {
            binding.minutes.text = singular[0]
            binding.hours.text = singular[1]
            binding.days.text = singular[2]
        } else {
            binding.minutes.text = plural[0]
            binding.hours.text = plural[1]
            binding.days.text = plural[2]
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
            binding.minutes.isChecked -> getString(R.string.min)
            binding.hours.isChecked -> getString(R.string.hours)
            binding.days.isChecked -> getString(R.string.days)
            else -> ""
        }
    }

    private fun View.updateDescription() {
        // update text string
        if (!binding.itemSwitch.isChecked) {
            binding.nextSync.text = getString(R.string.sync_disabled)
        } else {
            val parseInput = binding.input.text.toString()
                .let { it.ifEmpty { "0" } }.toInt()
                .let { if (it < 15) 15 else it }

            // retrieve correct word pronunciation before writing on screen
            binding.nextSync.text = getString(R.string.sync_every, parseInput, whichOneIsSelected())
        }
    }

    companion object {
        private const val TAG = "[ABOUT_DIALOG]"

        /** Shows the about dialog inside of [activity]. */
        fun show(activity: FragmentActivity) {
            val dialog = DialogBackgroundSync()
            dialog.show(activity.supportFragmentManager, TAG)
        }
    }
}
