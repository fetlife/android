package com.bitlove.fetlife

//TODO 1: Login
//TODO 2: Job Manager Fine Tune
//TODO 3: Push notification with poll support
//TODO 4: Settings with Personalization
//TODO 5: Turbolink link handling
//TODO 6: Further screens

import android.annotation.SuppressLint
import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import com.bitlove.fetlife.model.db.FetLifeContentDatabase
import android.support.annotation.LayoutRes
import android.support.annotation.RawRes
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.birbit.android.jobqueue.JobManager
import com.bitlove.fetlife.model.FetLifeDataSource
import com.bitlove.fetlife.model.network.FetLifeService
import com.google.gson.Gson
import org.jetbrains.anko.coroutines.experimental.bg
import com.birbit.android.jobqueue.config.Configuration
import android.os.Build
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.bitlove.fetlife.model.db.FetLifeUserDatabase
import com.bitlove.fetlife.view.navigation.NavigationFragmentFactory
import com.bitlove.fetlife.view.widget.FrescoImageLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.net.URI
import java.security.NoSuchAlgorithmException

//TODO check out developer book: https://antonioleiva.com/kotlin-android-developers-book/

//TODO check out warnings

class FetLifeApplication : Application() {

    companion object {
        lateinit var instance : FetLifeApplication
    }

    var loggedInUser: String? = null
    lateinit var fetLifeUserDatabase: FetLifeUserDatabase
    lateinit var fetLifeContentDatabase: FetLifeContentDatabase
    lateinit var fetlifeService : FetLifeService
    lateinit var fetlifeDataSource: FetLifeDataSource
    lateinit var jobManager: JobManager
    lateinit var navigationFragmentFactory: NavigationFragmentFactory

    override fun onCreate() {
        super.onCreate()
        instance = this

        fetLifeUserDatabase = Room.databaseBuilder(this, FetLifeUserDatabase::class.java, "fetlife_user_database").build()
        fetlifeService = FetLifeService()
        fetlifeDataSource = FetLifeDataSource()
        jobManager = createJobManager()
        navigationFragmentFactory = NavigationFragmentFactory()

        FrescoImageLoader.initFrescoImageLibrary(this)

    }

    fun onUserLoggedIn(userName: String, accessToken: String, refreshToken: String?) {
        fetlifeService.authHeader = accessToken
        fetlifeService.refreshToken = refreshToken
        if (loggedInUser != userName) {
            loggedInUser = userName
            //TODO: close db
            fetLifeContentDatabase = Room.databaseBuilder(this, FetLifeContentDatabase::class.java, "fetlife_database_" + userName).build()
        }
    }

    fun onUserLoggedOut() {
        if (loggedInUser == null) {
            return
        }

        bg {
            fetLifeUserDatabase.userDao().delete(loggedInUser!!)
        }

        loggedInUser = null
        //TODO: assign value to clear memory instead of null
        fetlifeService.authHeader = null
        fetlifeService.refreshToken = null
        //TODO: close database carefully (jobs might want to use it)
        fetLifeContentDatabase.close()
    }

    private fun createJobManager(): JobManager {
        //TODO solve db reference / opening
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

//TODO: solve flickering, consider:
//https://github.com/bumptech/glide/issues/729
//https://github.com/bumptech/glide/issues/2194
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

fun String.getBaseUrl() : String {
    return try {
        val uri = URI(this)
        URI(uri.getScheme(),uri.getAuthority(),uri.getPath(),null, null).toString()
    } catch (t: Throwable) {
        this
    }
}

fun RecyclerView.workaroundItemFlickeringOnChange() {
    this.itemAnimator = null
//    this.itemAnimator.changeDuration = 0
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

fun String.hash(): String {
    try {
        // Create MD5 Hash
        val digest = java.security.MessageDigest.getInstance("MD5")
        digest.update(toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuffer()
        for (i in messageDigest.indices)
            hexString.append(Integer.toHexString(0xFF and messageDigest[i].toInt()))
        return hexString.toString()
    } catch (e: NoSuchAlgorithmException) {
        //TODO log
    }
    return this
}

fun SharedPreferences.putStrings(vararg strings: String?) {
    val editor = edit()
    for (i in strings.indices step 2) {
        editor.putString(strings[i],strings[i+1])
    }
    editor.apply()
}

//TODO organise these functions
//TODO check out kotlin tricks and functions:
// https://antonioleiva.com/kotlin-awesome-tricks-for-android/
// https://android.github.io/android-ktx/core-ktx/alltypes/index.html
// https://github.com/Kotlin/anko
//



