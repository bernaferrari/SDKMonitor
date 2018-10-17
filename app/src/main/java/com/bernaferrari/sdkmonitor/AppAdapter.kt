package com.bernaferrari.sdkmonitor

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.bold
import com.bernaferrari.sdkmonitor.data.App
import com.bernaferrari.sdkmonitor.extensions.darken
import com.bernaferrari.sdkmonitor.extensions.setTextAsync
import com.bumptech.glide.Glide
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.row_navigation_item.view.*
import kotlinx.coroutines.experimental.*
import kotlin.coroutines.experimental.CoroutineContext

class AppAdapter(var itemsList: List<App>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        job.cancel()
        holder.itemView.icon
        super.onViewDetachedFromWindow(holder)
    }

    private var drawable: Drawable? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.row_navigation_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val snap = itemsList[position]

        holder.itemView.label.setTextAsync(snap.title)

        val s = SpannableStringBuilder().bold { append("28") }

        holder.itemView.minSdk.setTextAsync(s)

//        holder.itemView.card.setCardBackgroundColor(snap.backgroundColor)
        holder.itemView.bottom_view.background = ColorDrawable(snap.backgroundColor.darken)

        job = Job()
        runBlocking {
            val drawable =
                withContext(Dispatchers.IO) { AppManager.getIconFromId(snap.packageName) }

            // Glide performs a lot better than setImageDrawable
            Glide.with(holder.itemView.context)
                .load(drawable)
                .into(holder.itemView.icon)
        }

//        if (drawable == null) {
//            runBlocking {
//                updateDrawable(app)
//                loadGlideInto(holder, app)
//            }
//        } else {
//            loadGlideInto(holder, app)
//        }
    }

    private suspend inline fun updateDrawable(snap: App) {
        if (drawable == null) {
            drawable = withContext(Dispatchers.IO) { AppManager.getIconFromId(snap.packageName) }
        }
    }

    private fun loadGlideInto(viewHolder: RecyclerView.ViewHolder, snap: App) {
        // Using the application context avoids
        // IllegalArgumentException: You cannot start a load for a destroyed activity
        // which might happen when the user is scrolling and closes the app.
        // I believe the cost of using AppContext here is possibly less than checking if the
        // context is alive on every interaction.

//        Glide.with(Injector.get().appContext()).
        viewHolder.itemView.icon.setImageDrawable(AppManager.getIconFromId(snap.packageName))

        Glide.with(viewHolder.itemView.context)
            .load(drawable)
            .into(viewHolder.itemView.icon)
    }

    fun setDataSource(newList: List<App>) {

        val diffResult = runBlocking(Dispatchers.IO) {
            DiffUtil.calculateDiff(EmployeeDiffCallback(itemsList, newList))
        }

        itemsList = newList
        diffResult.dispatchUpdatesTo(this)
    }


    override fun getItemCount(): Int = itemsList.size
}

class EmployeeDiffCallback(
    private val mOldEmployeeList: List<App>,
    private val mNewEmployeeList: List<App>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = mOldEmployeeList.size

    override fun getNewListSize(): Int = mNewEmployeeList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return mOldEmployeeList[oldItemPosition].packageName === mNewEmployeeList[newItemPosition].packageName
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldEmployee = mOldEmployeeList[oldItemPosition]
        val newEmployee = mNewEmployeeList[newItemPosition]
        return oldEmployee.packageName == newEmployee.packageName
    }
}