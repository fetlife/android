package com.bitlove.fetlife.common.logic.databinding

//To solve the chicken-egg problem of the generated binding class
class DatabindingIdBinder {

    companion object {
        @JvmStatic
        var release: Int = 1
    }

}