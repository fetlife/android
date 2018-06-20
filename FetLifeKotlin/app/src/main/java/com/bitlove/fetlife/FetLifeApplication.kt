package com.bitlove.fetlife

import android.annotation.SuppressLint
import android.app.*
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
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
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import com.bitlove.fetlife.model.dataobject.wrapper.User
import com.bitlove.fetlife.model.db.FetLifeContentDatabaseWrapper
import com.bitlove.fetlife.model.db.FetLifeUserDatabase
import com.bitlove.fetlife.view.navigation.NavigationFragmentFactory
import com.bitlove.fetlife.view.widget.FrescoImageLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import java.net.URI
import java.security.NoSuchAlgorithmException
import org.joda.time.format.DateTimeFormatterBuilder
import org.joda.time.format.ISODateTimeFormat


//TODO check out developer book: https://antonioleiva.com/kotlin-android-developers-book/
//TODO check out warnings
//TODO add privacy policy (crashlytics)
//TODO cleanup progressTrackers
class FetLifeApplication : Application() {

    companion object {
        lateinit var instance : FetLifeApplication
    }

    var loggedInUser: User? = null
    lateinit var fetLifeUserDatabase: FetLifeUserDatabase
    lateinit var fetLifeContentDatabaseWrapper: FetLifeContentDatabaseWrapper
    lateinit var fetlifeService : FetLifeService
    lateinit var fetlifeDataSource: FetLifeDataSource
    lateinit var jobManager: JobManager
    lateinit var navigationFragmentFactory: NavigationFragmentFactory

    override fun onCreate() {
        super.onCreate()
        instance = this

        Fabric.with(this, Crashlytics())

        fetLifeUserDatabase = Room.databaseBuilder(this, FetLifeUserDatabase::class.java, "fetlife_user_database").fallbackToDestructiveMigration().build()
        fetLifeContentDatabaseWrapper = FetLifeContentDatabaseWrapper()
        fetlifeService = FetLifeService()
        fetlifeDataSource = FetLifeDataSource()
        jobManager = createJobManager()
        navigationFragmentFactory = NavigationFragmentFactory()

        FrescoImageLoader.initFrescoImageLibrary(this)

    }

    fun onUserLoggedIn(user: User, authHeader: String, refreshToken: String?) {
        fetlifeService.authHeader = authHeader
        fetlifeService.refreshToken = refreshToken
        if (loggedInUser != user) {
            //TODO: close db
            fetLifeContentDatabaseWrapper.init(user.getLocalId())
        }
        loggedInUser = user
    }

    fun onUserLoggedOut() {
        if (loggedInUser == null) {
            return
        }

        val userId = loggedInUser!!.getLocalId()!!
        bg {
            //TODO: cancel all jobs for the current user
            fetLifeUserDatabase.userDao().delete(userId)
            fetLifeContentDatabaseWrapper.safeRelease(userId)
        }

        loggedInUser = null
        //TODO: assign value to clear memory instead of null
        fetlifeService.authHeader = null
        fetlifeService.refreshToken = null
        //TODO: close database carefully (jobs might want to use it)
    }

    private fun createJobManager(): JobManager {
        //TODO solve db reference / opening
        //TODO add dispatcher and wake it up
        val builder = Configuration.Builder(this)
        return JobManager(builder.build())
    }

}

fun getLoggedInUser() : User? {
    return FetLifeApplication.instance.loggedInUser
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
//
//fun <DH> getLivePagesList(sourceFactory: DataSource.Factory<Int,DH>, pageSize: Int, boundaryCallback: PagedList.BoundaryCallback<DH>? = null): LiveData<PagedList<DH>> {
//    return LivePagedListBuilder<Int,DH>(sourceFactory,PagedList.Config.Builder().setPageSize(pageSize).setPrefetchDistance(pageSize).setInitialLoadSizeHint(pageSize).setEnablePlaceholders(true).build()).setBoundaryCallback(boundaryCallback).build()
//}

fun <DataBinding : ViewDataBinding> LayoutInflater.inflateBinding(@LayoutRes resId: Int, container: ViewGroup?, attachToRoot: Boolean = false) : DataBinding {
    return DataBindingUtil.inflate(this,resId,container,attachToRoot)
}

fun Fragment.closeKeyboard() {
    if (view != null) {
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}

fun Class<out ViewModel>.getViewModel(activity: FragmentActivity) : ViewModel {
    return ViewModelProviders.of(activity).get(this)
}

inline fun FragmentManager.inTransaction(func: android.support.v4.app.FragmentTransaction.() -> FragmentTransaction) {
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

fun String.countOccurance(subString: String) : Int {
    return split(subString).size - 1
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

fun String.shareExternal(context: Context) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, this)
    sendIntent.type = "text/plain"
    context.startActivity(sendIntent)
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

fun String.parseServerTime() : Long {
    return ISODateTimeFormat.dateTime().parseDateTime(this).millis
}

fun Long.toServerTime() : String {
    //2017-01-24 16:52:33.074 +0200'

    val dateTimeFormatter = DateTimeFormatterBuilder().
            appendYear(4,4).
            appendLiteral('-').
            appendMonthOfYear(2).
            appendLiteral('-').
            appendDayOfMonth(2).
            appendLiteral(' ').
            appendHourOfDay(2).
            appendLiteral(':').
            appendMinuteOfHour(2).
            appendLiteral(':').
            appendSecondOfMinute(2).
            appendLiteral('.').
            appendMillisOfSecond(3).
            appendLiteral(' ').
            appendTimeZoneOffset(null,false,2,2).toFormatter();

    return dateTimeFormatter.print(this)
}

fun SharedPreferences.putBoolean(name: String, value: Boolean) {
    val editor = edit()
    editor.putBoolean(name,value)
    editor.apply()
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



