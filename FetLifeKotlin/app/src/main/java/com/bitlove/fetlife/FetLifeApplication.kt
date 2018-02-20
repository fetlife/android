package com.bitlove.fetlife

import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.bitlove.fetlife.datasource.db.dao.FetlifeDatabase
import com.bitlove.fetlife.datasource.dataobject.Conversation
import android.os.AsyncTask
import android.support.annotation.LayoutRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bitlove.fetlife.datasource.FetLifeDataSouce
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import org.jetbrains.anko.coroutines.experimental.bg

class FetLifeApplication : Application() {

    companion object {
        lateinit var instance : FetLifeApplication
    }

    lateinit var fetlifeDatabase : FetlifeDatabase
    lateinit var fetlifeDataSource: FetLifeDataSouce

    override fun onCreate() {
        super.onCreate()
        instance = this

        fetlifeDatabase = Room.databaseBuilder(this, FetlifeDatabase::class.java, "fetlife_database").build()
        fetlifeDataSource = FetLifeDataSouce()

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


