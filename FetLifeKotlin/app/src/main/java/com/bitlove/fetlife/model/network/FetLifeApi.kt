package com.bitlove.fetlife.model.network

import com.bitlove.fetlife.model.dataobject.Conversation
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import com.bitlove.fetlife.model.network.networkobject.Token
import retrofit2.Call
import retrofit2.http.*

interface FetLifeApi {
    @POST("/api/oauth/token")
    fun login(@Query("client_id") clientId: String, @Query("client_secret") clientSecret: String, @Query("redirect_uri") redirectUrl: String, @Body authBody: AuthBody): Call<Token>

    @FormUrlEncoded
    @POST("/api/oauth/token")
    fun refreshToken(@Query("client_id") clientId: String, @Field("client_secret") clientSecret: String, @Field("redirect_uri") redirectUrl: String, @Field("grant_type") grantType: String, @Field("refresh_token") refreshToken: String): Call<Token>

//    @GET("/api/v2/me")
//    fun getMe(@Header("Authorization") authHeader: String): Call<Member>

    @GET("/api/v2/me/conversations")
    fun getConversations(@Header("Authorization") authHeader: String, @Query("order_by") orderBy: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<Conversation>>

    @GET("/api/v2/me/conversations/{conversationId}")
    fun getConversation(@Header("Authorization") authHeader: String, @Path("conversationId") conversationId: String): Call<Conversation>

}