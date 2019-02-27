//package com.bernaferrari.sdkmonitor.settings
//
//import android.content.Context
//import com.bernaferrari.sdkmonitor.R
//import com.bernaferrari.sdkmonitor.extensions.doOnChanged
//import com.xwray.groupie.kotlinandroidextensions.Item
//import com.xwray.groupie.kotlinandroidextensions.ViewHolder
//import kotlinx.android.synthetic.main.settings_item_interval.*
//
///**
// * Creates an interval item. This will be used on settings to track the sync period.
// *
// * @param title the item title
// * @param initialDelay the initial delay when item is created
// * @param listener callback for the result
// */
//class DialogItemInterval(
//    val title: String,
//    val initialDelay: Int,
//    val listener: (Long) -> (Unit)
//) : Item() {
//    val minutes = arrayOf(30, 60, 120, 360, 720, 1440, 2880, 10080, 20160)
//    var progress: Int = minutes.indexOfFirst { it == initialDelay }
//
//    override fun getLayout() = R.layout.settings_item_interval
//
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.seekBar.progress = progress
//
//        viewHolder.seekBar.doOnChanged { _, seekbar_progress, _ ->
//            progress = seekbar_progress
//            listener.invoke(minutes[progress].toLong())
//            viewHolder.progress.text = getTimeString(viewHolder.seekBar.context)
//        }
//        viewHolder.progress.text = getTimeString(viewHolder.seekBar.context)
//        viewHolder.title.text = title
//    }
//
//    private fun getTimeString(context: Context): String = minutes[progress].let {
//        when {
//            it < 60 -> "$it " + context.getString(R.string.min)
//            it == 60 -> context.getString(R.string.hour)
//            it <= 1440 -> "${it / 60} " + context.getString(R.string.hours)
//            else -> "${it / 1440} " + context.getString(R.string.days)
//        }
//    }
//}
