package com.bitlove.fetlife.model.network

import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.dataobject.Conversation
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import com.bitlove.fetlife.model.network.networkobject.Token
import com.bitlove.fetlife.readRawListResource
import com.google.gson.GsonBuilder
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//TODO : Consider using real mock: https://stackoverflow.com/questions/35748656/android-unit-test-with-retrofit2-and-mockito-or-robolectric
class FetLifApiStub : FetLifeApi {

    private val gson = GsonBuilder().create()

    override fun refreshToken(clientId: String, clientSecret: String, redirectUrl: String, grantType: String, refreshToken: String): Call<Token> {
        return null!!
    }

    override fun getConversations(authHeader: String, orderBy: String?, limit: Int?, page: Int?): Call<Array<Conversation>> {
        return CallStub(gson.readRawListResource(R.raw.conversations,Array<Conversation>::class.java))
    }

    override fun getConversation(authHeader: String, conversationId: String): Call<Conversation> {
        return null!!
    }

    override fun login(clientId: String, clientSecret: String, redirectUrl: String, authBody: AuthBody): Call<Token> {
        return null!!
    }

}

class CallStub<T : Any>(private val result: T) : Call<T> {

    override fun execute(): Response<T> {
        Thread.sleep(1500)
        return Response.success(result)
    }

    override fun isExecuted(): Boolean {
        return true
    }

    override fun clone(): Call<T> {
        return this
    }

    override fun isCanceled(): Boolean {
        return false
    }

    override fun cancel() {
    }

    override fun request(): Request {
        return null!!
    }

    override fun enqueue(callback: Callback<T>?) {
    }
}

