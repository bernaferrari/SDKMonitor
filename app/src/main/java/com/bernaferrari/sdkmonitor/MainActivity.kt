package com.bernaferrari.sdkmonitor

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.airbnb.mvrx.BaseMvRxActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Injector.get().sharedPrefs().getBoolean("light mode", true)) {
            setTheme(R.style.AppThemeLight)
        } else {
            setTheme(R.style.AppThemeDark)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavigationUI.setupWithNavController(
            bottom_nav_view,
            my_nav_host_fragment.findNavController()
        )
    }
}
