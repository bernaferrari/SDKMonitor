package com.bernaferrari.sdkmonitor.settings

import android.app.Dialog
import android.os.Bundle
import com.bernaferrari.sdkmonitor.Injector
import com.bernaferrari.sdkmonitor.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * BottomSheetDialog fragment that uses a custom
 * theme which sets a rounded background to the dialog
 * and doesn't dim the navigation bar
 */
open class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val isDarkMode = Injector.get().sharedPrefs().getBoolean("dark", true)

    override fun getTheme(): Int = if (isDarkMode) {
        R.style.BottomSheetDialogThemeDark
    } else {
        R.style.BottomSheetDialogThemeLight
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

}