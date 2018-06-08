package com.bitlove.fetlife.model.network

import com.bitlove.fetlife.model.dataobject.entity.content.*
import com.bitlove.fetlife.model.dataobject.wrapper.Reaction
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import com.bitlove.fetlife.model.network.networkobject.Feed
import com.bitlove.fetlife.model.network.networkobject.Token
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface FetLifeApi {

    @POST("/api/oauth/token")
    fun login(@Query("client_id") clientId: String, @Query("client_secret") clientSecret: String, @Query("redirect_uri") redirectUrl: String, @Body authBody: AuthBody): Call<Token>

    @FormUrlEncoded
    @POST("/api/oauth/token")
    fun refreshToken(@Query("client_id") clientId: String, @Field("client_secret") clientSecret: String, @Field("redirect_uri") redirectUrl: String, @Field("grant_type") grantType: String, @Field("refresh_token") refreshToken: String): Call<Token>

    @GET("/api/v2/me")
    fun getMe(@Header("Authorization") authHeader: String): Call<MemberEntity>

    @GET("/api/v2/me/conversations")
    fun getConversations(@Header("Authorization") authHeader: String, @Query("order_by") orderBy: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ContentEntity>>

    @GET("/api/v2/me/conversations/{conversationId}")
    fun getConversation(@Header("Authorization") authHeader: String, @Path("conversationId") conversationId: String): Call<ContentEntity>

    @GET("/api/v2/me/conversations/{conversationId}/messages")
    fun getMessages(@Header("Authorization") authHeader: String, @Path("conversationId") conversationId: String?, @Query("since_id") sinceMessageId: String?, @Query("until_id") untilMessageId: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ReactionEntity>>

    @FormUrlEncoded
    @POST("/api/v2/me/conversations/{conversationId}/messages")
    fun postMessage(@Header("Authorization") authHeader: String, @Path("conversationId") conversationId: String, @Field("body") body: String): Call<ReactionEntity>

    @GET("/api/v2/members/{memberId}/{entityType}/{entityId}/comments")
    fun getComments(@Header("Authorization") authHeader: String, @Path("memberId") memberId: String?, @Path("entityType") entityType: String?, @Path("entityId") entityId: String?, @Query("since_id") sinceCommentId: String?, @Query("until_id") untilCommentId: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ReactionEntity>>

    @FormUrlEncoded
    @POST("/api/v2/members/{memberId}/{entityType}/{entityId}/comments")
    fun postComment(@Header("Authorization") authHeader: String, @Path("memberId") memberId: String?, @Path("entityType") entityType: String?, @Path("entityId") entityId: String?, @Field("body") body: String): Call<ReactionEntity>

    @GET("/api/v2/me/feed")
    fun getFriendsFeed(@Header("Authorization") authHeader: String, @Query("marker") timeStamp: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Feed>

    @GET("/api/v2/explore/fresh-and-pervy")
    fun getFreshAndPervy(@Header("Authorization") authHeader: String, @Query("until") timeStamp: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ExploreStoryEntity>>

    @GET("/api/v2/explore/kinky-and-popular")
    fun getKinkyAndPopular(@Header("Authorization") authHeader: String, @Query("until") timeStamp: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ExploreStoryEntity>>

    @GET("/api/v2/explore/stuff-you-love")
    fun getStuffYouLove(@Header("Authorization") authHeader: String, @Query("until") timeStamp: String?, @Query("limit") limit: Int?, @Query("page") page: Int?): Call<Array<ExploreStoryEntity>>

    @PUT("/api/v2/me/loves/{content_type}/{content_id}")
    fun putLove(@Header("Authorization") authHeader: String, @Path("content_id") contentId: String, @Path("content_type") contentType: String): Call<ResponseBody>

    @DELETE("/api/v2/me/loves/{content_type}/{content_id}")
    fun deleteLove(@Header("Authorization") authHeader: String, @Path("content_id") contentId: String, @Path("content_type") contentType: String): Call<ResponseBody>

    @GET("/api/v2/members/{member_id}/memberships")
    fun getGroups(@Header("Authorization") authHeader: String, @Path("member_id") memberId: String?, @Query("limit") limit: Int, @Query("page") page: Int?): Call<Array<RelationEntity>>

}