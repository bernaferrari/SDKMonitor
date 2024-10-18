package com.bernaferrari.sdkmonitor

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bernaferrari.sdkmonitor.databinding.ActivityMainBinding
import com.bernaferrari.sdkmonitor.extensions.isDarkMode

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Injector.get().isLightTheme().isSet()) {
            if (Injector.get().isLightTheme().get()) {
                setTheme(R.style.AppThemeLight)
            } else {
                setTheme(R.style.AppThemeDark)
            }
        } else {
            setAndroidTheme()
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.let { fragment ->
            NavigationUI.setupWithNavController(
                binding.bottomNav,
                fragment.findNavController()
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (!Injector.get().isLightTheme().isSet()) {
            setAndroidTheme()
        }
    }

    private fun setAndroidTheme() {
        if (isDarkMode) {
            setTheme(R.style.AppThemeDark)
        } else {
            setTheme(R.style.AppThemeLight)
        }
    }
}
