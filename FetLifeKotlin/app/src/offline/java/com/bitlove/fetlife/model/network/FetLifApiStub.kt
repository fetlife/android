package com.bitlove.fetlife.model.network

import com.bitlove.fetlife.R
import com.bitlove.fetlife.model.dataobject.base.Comment
import com.bitlove.fetlife.model.dataobject.base.Conversation
import com.bitlove.fetlife.model.dataobject.temp.ExploreStory
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import com.bitlove.fetlife.model.network.networkobject.Token
import com.bitlove.fetlife.readRawListResource
import com.google.gson.GsonBuilder
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

//TODO : Consider using real mock: https://stackoverflow.com/questions/35748656/android-unit-test-with-retrofit2-and-mockito-or-robolectric
class FetLifApiStub : FetLifeApi {

    private val gson = GsonBuilder().create()

    override fun getStuffYouLove(authHeader: String, timeStamp: String, limit: Int?, page: Int?): Call<Array<ExploreStory>> {
        return CallStub(gson.readRawListResource(R.raw.stuff_you_love,Array<ExploreStory>::class.java))
    }

    override fun getMessages(authHeader: String, conversationId: String, sinceMessageId: String, untilMessageId: String, limit: Int): Call<Array<Comment>> {
        val messages = gson.readRawListResource(R.raw.messages,Array<Comment>::class.java)
        for (message in messages) {
            message.id = conversationId + message.id
        }
        return CallStub(messages)
    }

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
        Thread.sleep(3000)
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

