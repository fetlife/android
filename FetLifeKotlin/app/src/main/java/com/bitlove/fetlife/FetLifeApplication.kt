package com.bitlove.fetlife

import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.bitlove.fetlife.datasource.db.FetlifeDatabase
import com.bitlove.fetlife.datasource.dataobject.Conversation
import android.support.annotation.LayoutRes
import android.support.annotation.RawRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.birbit.android.jobqueue.JobManager
import com.bitlove.fetlife.datasource.FetLifeDataSouce
import com.bitlove.fetlife.datasource.network.FetLifeService
import com.google.gson.Gson
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.coroutines.experimental.bg
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService
import com.birbit.android.jobqueue.config.Configuration


class FetLifeApplication : Application() {

    companion object {
        lateinit var instance : FetLifeApplication
    }

    lateinit var fetlifeDatabase : FetlifeDatabase
    lateinit var fetlifeService : FetLifeService
    lateinit var fetlifeDataSource: FetLifeDataSouce
    lateinit var jobManager: JobManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        fetlifeDatabase = Room.databaseBuilder(this, FetlifeDatabase::class.java, "fetlife_database").build()
        fetlifeService = FetLifeService()
        fetlifeDataSource = FetLifeDataSouce()
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


