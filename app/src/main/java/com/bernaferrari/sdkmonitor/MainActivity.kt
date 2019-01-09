package com.bernaferrari.sdkmonitor

import android.os.Bundle
import com.airbnb.mvrx.BaseMvRxActivity

class MainActivity : BaseMvRxActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
