package com.bernaferrari.ui.extras

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MvRxView
import com.bernaferrari.ui.base.PERSISTED_VIEW_ID_KEY
import java.util.*

/**
 * Make your base Fragment class extend this to get Mavericks functionality.
 *
 * This is necessary for the view model delegates and persistence to work correctly.
 */
abstract class BaseMvRxDialogFragment : DialogFragment(), MvRxView {

    final override val mvrxViewId: String by lazy { mvrxPersistedViewId }

    private lateinit var mvrxPersistedViewId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        mvrxPersistedViewId =
            savedInstanceState?.getString(PERSISTED_VIEW_ID_KEY)
                    ?: "${this::class.java.simpleName}_${UUID.randomUUID()}"
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PERSISTED_VIEW_ID_KEY, mvrxViewId)
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }
}
