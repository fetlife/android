package com.bitlove.fetlife

import android.annotation.SuppressLint
import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.Context
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.bitlove.fetlife.model.db.FetLifeDatabase
import android.support.annotation.LayoutRes
import android.support.annotation.RawRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.birbit.android.jobqueue.JobManager
import com.bitlove.fetlife.model.FetLifeDataSource
import com.bitlove.fetlife.model.network.FetLifeService
import com.google.gson.Gson
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import com.birbit.android.jobqueue.config.Configuration
import android.databinding.BindingAdapter
import android.os.Build
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.bitlove.fetlife.view.navigation.NavigationFragmentFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

//TODO check out developer book: https://antonioleiva.com/kotlin-android-developers-book/

//TODO check out warnings

class FetLifeApplication : Application() {

    companion object {
        lateinit var instance : FetLifeApplication

        @BindingAdapter("layout_height")
        fun setLayoutHeight(view: View, height: Float) {
            val layoutParams = view.layoutParams
            layoutParams.height = height.toInt()
            view.layoutParams = layoutParams
        }

        @BindingAdapter("layout_width")
        fun setLayoutWidth(view: View, width: Float) {
            val layoutParams = view.layoutParams
            layoutParams.height = width.toInt()
            view.layoutParams = layoutParams
        }

    }

    lateinit var fetLifeDatabase: FetLifeDatabase
    lateinit var fetlifeService : FetLifeService
    lateinit var fetlifeDataSource: FetLifeDataSource
    lateinit var jobManager: JobManager
    lateinit var navigationFragmentFactory: NavigationFragmentFactory

    override fun onCreate() {
        super.onCreate()
        instance = this

        //TODO use database per user
        fetLifeDatabase = Room.databaseBuilder(this, FetLifeDatabase::class.java, "fetlife_database").build()
        fetlifeService = FetLifeService()
        fetlifeDataSource = FetLifeDataSource()
        jobManager = createJobManager()
        navigationFragmentFactory = NavigationFragmentFactory()

        //fill test data
        async(UI) {
            bg {
//                fetLifeDatabase.contentDao().deleteAll()
//                fetLifeDatabase.reactionDao().deleteAll()
            }
        }
    }

    private fun createJobManager(): JobManager {
        //TODO add dispatcher and wake it up
        val builder = Configuration.Builder(this)
        return JobManager(builder.build())
    }
}

//Extension functions

//fun View.visible() {
//    visibility = View.VISIBLE
//}
//
//fun View.gone() {
//    visibility = View.GONE
//}
//
//fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
//    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
//}

fun <DataBinding : ViewDataBinding> LayoutInflater.inflateBinding(@LayoutRes resId: Int, container: ViewGroup?, attachToRoot: Boolean = false) : DataBinding {
    return DataBindingUtil.inflate(this,resId,container,attachToRoot)
}

fun Class<out ViewModel>.getViewModel(activity: FragmentActivity) : ViewModel {
    return ViewModelProviders.of(activity).get(this)
}

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun Resources.readRawResource(@RawRes resourceId : Int) : String {
    val inputStream = this.openRawResource(resourceId)
    return inputStream.bufferedReader().use { it.readText() }
}

fun <T> Gson.readRawListResource(@RawRes resourceId : Int, arrayClass : Class<out Array<T>>) : Array<T> {
    val json = FetLifeApplication.instance.resources.readRawResource(resourceId)
    return this.fromJson(json,arrayClass)
}

fun ImageView.loadWithGlide(url : String?, placeHolderId : Int?) {
    var glide = Glide.with(context.applicationContext).load(url)
    if (placeHolderId != null) {
        glide = glide.apply(RequestOptions().placeholder(placeHolderId))
    }
    try {
        glide.into(this)
    } catch (t : Throwable) {
        //TODO log
    }
}

fun String.toUniqueLong() : Long {
    var h = 98764321261L
    val chars = this.toCharArray()

    for (char in chars) {
        h = 31*h + char.toInt()
    }
    return h
}

fun RecyclerView.workaroundItemFlickeringOnChange() {
    this.itemAnimator.changeDuration = 0
}

@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShiftMode() {
    val menuView = getChildAt(0) as BottomNavigationMenuView
    try {
        val shiftingMode = menuView::class.java.getDeclaredField("mShiftingMode")
        shiftingMode.isAccessible = true
        shiftingMode.setBoolean(menuView, false)
        shiftingMode.isAccessible = false
        for (i in 0 until menuView.childCount) {
            val item = menuView.getChildAt(i) as BottomNavigationItemView
            item.setShiftingMode(false)
            // set once again checked value, so view will be updated
            item.setChecked(item.itemData.isChecked)
        }
    } catch (e: Throwable) {
        //TODO log
    }
}

fun Context.getSafeColor(resId : Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(resId)
    } else {
        resources.getColor(resId)
    }
}

//TODO organise these functions
//TODO check out kotlin tricks and functions:
// https://antonioleiva.com/kotlin-awesome-tricks-for-android/
// https://android.github.io/android-ktx/core-ktx/alltypes/index.html
// https://github.com/Kotlin/anko
//



