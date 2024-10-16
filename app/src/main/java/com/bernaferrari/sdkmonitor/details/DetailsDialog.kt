package com.bernaferrari.sdkmonitor.details

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.airbnb.mvrx.fragmentViewModel
import com.bernaferrari.sdkmonitor.core.AppManager
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.databinding.DetailsFragmentBinding
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.ui.extras.BaseDaggerMvRxDialogFragment
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class DetailsDialog : BaseDaggerMvRxDialogFragment() {
    private var _binding: DetailsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val viewModel: DetailsViewModel by fragmentViewModel()
    @Inject
    lateinit var detailsViewModelFactory: DetailsViewModel.Factory

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = activity ?: blowUp()
        _binding = DetailsFragmentBinding.inflate(layoutInflater)

        val args = arguments ?: blowUp()
        val app = args.getParcelable(KEY_APP) as? App ?: blowUp()

        return MaterialDialog(context)
            .customView(null, binding.root, noVerticalPadding = true)
            .also { it.getCustomView().setUpViews(app) }
    }

    private fun View.setUpViews(app: App) {

        binding.titlecontent.text = app.title

        binding.titleBar.background = ColorDrawable(app.backgroundColor.darken.darken)

        binding.closecontent.setOnClickListener { dismiss() }

        binding.playStore.also {
            it.isVisible = app.isFromPlayStore
            it.setOnClickListener {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${app.packageName}")
                )

                startActivity(intent)
            }
        }

        binding.info.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:" + app.packageName)
            startActivity(intent)
        }

        binding.recycler.background = ColorDrawable(app.backgroundColor.darken)

        val detailsController = DetailsController()
        binding.recycler.setController(detailsController)

        runBlocking {
            val packageName = app.packageName
            val data = viewModel.fetchAppDetails(packageName)
            if (data.isEmpty()) {
                AppManager.removePackageName(packageName)
                this@DetailsDialog.dismiss()
            } else {
                val versions = viewModel.fetchAllVersions(packageName)
                detailsController.setData(data, versions)
            }
        }
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
}
