package com.bitlove.fetlife.common.logic.databinding

import kotlin.reflect.KProperty0
import kotlin.reflect.full.staticProperties

//To solve the chicken-egg problem of the generated binding class
class DataBindingIdBinder {

    companion object {
        @JvmStatic
        var release: Int = 1
            get() {
                return (Class.forName("com.bitlove.fetlife.BR").kotlin.staticProperties.firstOrNull { it.name == "release" } as? KProperty0<Int>)?.get()
                        ?: 1
            }
        @JvmStatic
        var releaseHandler: Int = 1
            get() {
                return (Class.forName("com.bitlove.fetlife.BR").kotlin.staticProperties.firstOrNull { it.name == "releaseHandler" } as? KProperty0<Int>)?.get()
                        ?: 1
            }
    }

}