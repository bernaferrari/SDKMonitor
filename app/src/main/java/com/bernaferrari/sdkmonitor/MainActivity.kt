package com.bernaferrari.sdkmonitor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bernaferrari.sdkmonitor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Injector.get().isLightTheme().get()) {
            setTheme(R.style.AppThemeLight)
        } else {
            setTheme(R.style.AppThemeDark)
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
}
