package com.bitlove.fetlife

import android.app.Activity
import android.app.Application
import android.arch.persistence.room.Room
import android.databinding.ViewDataBinding
import android.view.View
import android.widget.Toast
import com.bitlove.fetlife.datasource.db.dao.FetlifeDatabase
import com.bitlove.fetlife.datasource.dataobject.Conversation
import android.os.AsyncTask
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.reflect.KClass

class FetLifeApplication : Application() {

    companion object {
        var instance : FetLifeApplication? = null
    }

    lateinit var fetlifeDatabase : FetlifeDatabase

    override fun onCreate() {
        super.onCreate()
        instance = this
        fetlifeDatabase = Room.databaseBuilder(this, FetlifeDatabase::class.java, "fetlife_database").build()

        //fill test data
        AsyncTask.execute {
            fetlifeDatabase.conversationDao().deleteAll()
            fetlifeDatabase.conversationDao().insert(Conversation("",false,false,"","",System.currentTimeMillis().toString(),null,null,0))
        }
    }
}

//Extension functions

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

inline fun ViewGroup.inflateBinding(bindingClass: KClass<out Any>, attachToRoot: Boolean = false): Any? {
    var inflateMethod = bindingClass.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
    return inflateMethod.invoke(null,LayoutInflater.from(context), this, attachToRoot) as ViewDataBinding?
}

fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this,message,duration).show()
}


