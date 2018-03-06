package com.bitlove.fetlife

import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.bitlove.fetlife.model.db.FetlifeDatabase
import com.bitlove.fetlife.model.dataobject.Conversation
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
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.coroutines.experimental.bg
import com.birbit.android.jobqueue.config.Configuration
import android.databinding.BindingAdapter
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

//TODO check out developer book: https://antonioleiva.com/kotlin-android-developers-book/

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

    lateinit var fetlifeDatabase : FetlifeDatabase
    lateinit var fetlifeService : FetLifeService
    lateinit var fetlifeDataSource: FetLifeDataSource
    lateinit var jobManager: JobManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        //TODO use database per user
        fetlifeDatabase = Room.databaseBuilder(this, FetlifeDatabase::class.java, "fetlife_database").build()
        fetlifeService = FetLifeService()
        fetlifeDataSource = FetLifeDataSource()
        jobManager = createJobManager()

        //fill test data
        async(UI) {
            bg {
                fetlifeDatabase.conversationDao().deleteAll()
                fetlifeDatabase.conversationDao().insert(Conversation("",false,false,"","",System.currentTimeMillis().toString(),null,null,0))
            }
            delay(3000)
            bg {
                fetlifeDatabase.conversationDao().insert(Conversation(System.currentTimeMillis().toString(),false,false,"","",System.currentTimeMillis().toString(),null,null,0))
            }
            delay(3000)
            bg {
                fetlifeDatabase.conversationDao().insert(Conversation(System.currentTimeMillis().toString(),false,false,"","",System.currentTimeMillis().toString(),null,null,0))
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

//TODO check out kotlin tricks and functions:
// https://antonioleiva.com/kotlin-awesome-tricks-for-android/
// https://android.github.io/android-ktx/core-ktx/alltypes/index.html
// https://github.com/Kotlin/anko
//



