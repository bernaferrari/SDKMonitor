package com.bernaferrari.sdkmonitor.details

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.mvrx.activityViewModel
import com.bernaferrari.sdkmonitor.R
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.main.MainRxViewModel
import kotlinx.android.synthetic.main.details_fragment.view.*
import kotlinx.coroutines.runBlocking

class DetailsDialog : BaseMvRxDialogFragment() {

    private val viewModel: MainRxViewModel by activityViewModel()

    companion object {
        private const val TAG = "[DetailsDialog]"
        private const val KEY_APP = "app"

        fun <T> show(
            fragment: T,
            app: App
        ) where T : FragmentActivity {
            val dialog = DetailsDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_APP, app)
                }
            }

            val ft = fragment.supportFragmentManager
                .beginTransaction()
                .addToBackStack(TAG)

            dialog.show(ft, TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: blowUp()

        val args = arguments ?: blowUp()
        val app = args.getParcelable(KEY_APP) as? App ?: blowUp()

        val dialog = MaterialDialog(context).customView(
            R.layout.details_fragment,
            noVerticalPadding = true
        )
        dialog.show()

        val customView = dialog.getCustomView()

        customView.titlecontent.text = app.title

        customView.title_bar.background = ColorDrawable(app.backgroundColor.darken.darken)

        customView.closecontent.setOnClickListener { dialog.dismiss() }

        customView.play_store.also {
            it.isVisible = app.isFromPlayStore
            it.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${app.packageName}")
                )

                startActivity(intent)
            }
        }

        customView.info.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + app.packageName)
            startActivity(intent)
        }

        customView.recycler.also { epoxyRecycler ->

            epoxyRecycler.background = ColorDrawable(app.backgroundColor.darken)

            val detailsController = DetailsController()
            epoxyRecycler.setController(detailsController)

            runBlocking {
                val packageName = app.packageName
                val data = viewModel.fetchAppDetails(packageName)
                val versions = viewModel.fetchAllVersions(packageName)
                detailsController.setData(data, versions)
            }
        }

        return dialog
    }

    override fun invalidate() {

    }

    private fun <T> blowUp(): T {
        throw IllegalStateException("Oh no!")
    }

    override fun onStart() {
        super.onStart()
        // This ensures that invalidate() is called for static screens that don't
        // subscribe to a ViewModel.
        postInvalidate()
    }
}
