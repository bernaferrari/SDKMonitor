package com.bernaferrari.sdkmonitor

import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import com.airbnb.epoxy.EpoxyModel
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A pattern for using epoxy models with Kotlin with no annotations or code generation.
 *
 * See [com.airbnb.epoxy.kotlinsample.models.ItemDataClass] for a usage example.
 */
abstract class KotlinModel(
    @LayoutRes private val layoutRes: Int
) : EpoxyModel<View>(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    private var view: View? = null

    abstract fun bind()

    override fun bind(view: View) {
        job = Job()
        this.view = view
        bind()
    }

    override fun unbind(view: View) {
        job.cancel()
        this.view = null
    }

    override fun getDefaultLayout() = layoutRes

    protected fun <V : View> bind(@IdRes id: Int) = object : ReadOnlyProperty<KotlinModel, V> {
        override fun getValue(thisRef: KotlinModel, property: KProperty<*>): V {
            // This is not efficient because it looks up the view by id every time (it loses
            // the pattern of a "holder" to cache that look up). But it is simple to use and could
            // be optimized with a map
            @Suppress("UNCHECKED_CAST")
            return view?.findViewById(id) as V?
                    ?: throw IllegalStateException("View ID $id for '${property.name}' not found.")
        }
    }
}