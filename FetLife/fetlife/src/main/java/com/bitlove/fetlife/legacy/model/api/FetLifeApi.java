package com.bitlove.fetlife.legacy.model.api;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Conversation;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.FriendRequest;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Group;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupComment;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.GroupPost;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Member;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Message;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Picture;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Relationship;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Status;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Video;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Writing;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.AppId;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.AuthBody;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Feed;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.GroupMembership;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.NotificationCounts;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Rsvp;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Token;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.VideoUploadResult;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FetLifeApi {

    @POST("/api/oauth/token")
    Call<Token> login(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("redirect_uri") String redirectUrl, @Body() AuthBody authBody);

    @FormUrlEncoded
    @POST("/api/oauth/token")
    Call<Token> refreshToken(@Query("client_id") String clientId, @Field("client_secret") String clientSecret, @Field("redirect_uri") String redirectUrl, @Field("grant_type") String grantType, @Field("refresh_token") String refreshToken);

    @GET("/api/v2/me")
    Call<Member> getMe(@Header("Authorization") String authHeader);

    @GET("/api/v2/members/{memberId}/memberships")
    Call<List<GroupMembership>> getMemberGroupMemberships(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/me/conversations")
    Call<List<Conversation>> getConversations(@Header("Authorization") String authHeader, @Query("order_by") String orderBy, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/me/conversations/{conversationId}")
    Call<Conversation> getConversation(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId);

    @GET("/api/v2/me/notifications/counts")
    Call<NotificationCounts> getNotificationCounts(@Header("Authorization") String authHeader);

    @GET("/api/v2/me/friends")
    Call<List<Member>> getFriends(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/me/conversations/{conversationId}/messages")
    Call<List<Message>> getMessages(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Query("since_id") String sinceMessageId, @Query("until_id") String untilMessageId, @Query("limit") int limit);

    @GET("/api/v2/search/members")
    Call<List<Member>> searchMembers(@Header("Authorization") String authHeader, @Query("query") String query, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/search/groups")
    Call<List<Group>> searchGroups(@Header("Authorization") String authHeader, @Query("query") String query, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/groups/{groupId}/memberships")
    Call<List<GroupMembership>> getGroupMembers(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/groups/{groupId}/posts")
    Call<List<GroupPost>> getGroupDiscussions(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/groups/{groupId}/posts/{groupPostId}")
    Call<GroupPost> getGroupDiscussion(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Path("groupPostId") String groupPostId);

    @GET("/api/v2/groups/{groupId}/posts/{groupPostId}/comments")
    Call<List<GroupComment>> getGroupMessages(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Path("groupPostId") String groupPostId, @Query("limit") int limit, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/v2/groups/{groupId}/posts/{groupPostId}/comments")
    Call<GroupComment> postGroupMessage(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Path("groupPostId") String groupPostId, @Field("body") String body);

    @GET("/api/v2/members/{memberId}")
    Call<Member> getMember(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/events/{eventId}")
    Call<Event> getEvent(@Header("Authorization") String authHeader, @Path("eventId") String memberId);

    @GET("/api/v2/groups/{groupId}")
    Call<Group> getGroup(@Header("Authorization") String authHeader, @Path("groupId") String memberId);

    @PUT("/api/v2/groups/{groupId}/join")
    Call<ResponseBody> joinGroup(@Header("Authorization") String authHeader, @Path("groupId") String groupId);

    @DELETE("/api/v2/groups/{groupId}/join")
    Call<ResponseBody> leaveGroup(@Header("Authorization") String authHeader, @Path("groupId") String groupId);

    @PUT("/api/v2/groups/{groupId}/posts/{groupPostId}/follow")
    Call<ResponseBody> followDiscussion(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Path("groupPostId") String groupPostId);

    @DELETE("/api/v2/groups/{groupId}/posts/{groupPostId}/follow")
    Call<ResponseBody> unfollowDiscussion(@Header("Authorization") String authHeader, @Path("groupId") String groupId, @Path("groupPostId") String groupPostId);

    @GET("/api/v2/me/rsvps")
    Call<List<Rsvp>> getRsvps(@Header("Authorization") String authHeader, @Query("event_id") String eventId);

    @GET("/api/v2/members/{memberId}/latest_activity")
    Call<Feed> getMemberFeed(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/relationships")
    Call<List<Relationship>> getMemberRelationship(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/members/{memberId}/pictures")
    Call<List<Picture>> getMemberPictures(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/pictures/{pictureId}")
    Call<Picture> getMemberPicture(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Path("pictureId") String pictureId);

    @GET("/api/v2/members/{memberId}/videos")
    Call<List<Video>> getMemberVideos(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/videos/{videoId}")
    Call<Video> getMemberVideo(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Path("videoId") String videoId);

    @GET("/api/v2/members/{memberId}/friends")
    Call<List<Member>> getMemberFriends(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/followers")
    Call<List<Member>> getMemberFollowers(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/following ")
    Call<List<Member>> getMemberFollowees(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/statuses")
    Call<List<Status>> getMemberStatuses(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/rsvps")
    Call<List<Rsvp>> getMemberRsvps(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/members/{memberId}/writings/{writingId}")
    Call<Writing> getWriting(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Path("writingId") String writingId);

    @GET("/api/v2/members/{memberId}/writings")
    Call<List<Writing>> getMemberWritings(@Header("Authorization") String authHeader, @Path("memberId") String memberId, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/events/{eventId}/rsvps")
    Call<List<Rsvp>> getEventRsvps(@Header("Authorization") String authHeader, @Path("eventId") String eventId, @Query("status") String status, @Query("limit") int limit, @Query("page") int page);

    @FormUrlEncoded
    @POST("/api/v2/me/conversations/{conversationId}/messages")
    Call<Message> postMessage(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("body") String body);

    @FormUrlEncoded
    @PUT("/api/v2/me/conversations/{conversationId}/messages/read")
    Call<ResponseBody> setMessagesRead(@Header("Authorization") String authHeader, @Path("conversationId") String conversationId, @Field("ids") String[] ids);

    @FormUrlEncoded
    @POST("/api/v2/me/conversations")
    Call<Conversation> postConversation(@Header("Authorization") String authHeader, @Field("user_id") String userId, @Field("subject") String subject, @Field("body") String body);

    @GET("/api/v2/me/friendrequests")
    Call<List<FriendRequest>> getFriendRequests(@Header("Authorization") String authHeader, @Query("filter") String filter, @Query("limit") int limit, @Query("page") int page);

    @PUT("/api/v2/me/friendrequests/{friendRequestId}")
    Call<FriendRequest> acceptFriendRequests(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId);

    @DELETE("/api/v2/me/friendrequests/{friendRequestId}")
    Call<FriendRequest> cancelFriendRequest(@Header("Authorization") String authHeader, @Path("friendRequestId") String friendRequestId);

    @FormUrlEncoded
    @POST("/api/v2/me/friendrequests")
    Call<FriendRequest> createFriendRequest(@Header("Authorization") String authHeader, @Field("member_id") String friendId);

    @PUT("/api/v2/members/{memberId}/follow")
    Call<ResponseBody> createFollow(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @DELETE("/api/v2/members/{memberId}/follow")
    Call<ResponseBody> removeFollow(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @DELETE("api/v2/me/relations/{memberId}")
    Call<ResponseBody> removeFriendship(@Header("Authorization") String authHeader, @Path("memberId") String memberId);

    @GET("/api/v2/me/feed")
    Call<Feed> getFeed(@Header("Authorization") String authHeader, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/explore/stuff-you-love")
    Call<List<Story>> getStuffYouLove(@Header("Authorization") String authHeader, @Query("marker") String timeStamp, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/explore/fresh-and-pervy")
    Call<List<Story>> getFreshAndPervy(@Header("Authorization") String authHeader, @Query("until") String timeStamp, @Query("limit") int limit, @Query("page") Integer page);

    @GET("/api/v2/explore/kinky-and-popular")
    Call<List<Story>> getKinkyAndPopular(@Header("Authorization") String authHeader, @Query("until") String timeStamp, @Query("limit") int limit, @Query("page") int page);

    @PUT("/api/v2/me/loves/{content_type}/{content_id}")
    Call<ResponseBody> putLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType);

    @PUT("/api/v2/me/rsvps")
    Call<ResponseBody> putRsvp(@Header("Authorization") String authHeader, @Query("event_id") String contentId, @Query("status") String rsvpsType);

    @DELETE("/api/v2/me/loves/{content_type}/{content_id}")
    Call<ResponseBody> deleteLove(@Header("Authorization") String authHeader, @Path("content_id") String contentId, @Path("content_type") String contentType);

    @FormUrlEncoded
    @POST("/api/v2/me/videos/uploads")
    Call<VideoUploadResult> uploadVideoStart(@Header("Authorization") String authHeader, @Field("title") String title, @Field("description") String description, @Field("video_file_name") String videoFileName, @Field("only_friends") boolean friendsOnly, @Field("is_of_or_by_user") boolean isFromUser);

    @POST("/api/v2/me/videos/uploads/{video_upload_id}/finish")
    Call<ResponseBody> uploadVideoFinish(@Header("Authorization") String authHeader, @Path("video_upload_id") String videoUploadId);

    @GET("/api/v2/search/events/by_location")
    Call<List<Event>> searchEvents(@Header("Authorization") String authHeader, @Query("latitude") double latitude, @Query("longitude") double longitude, @Query("range") double range, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/search/events")
    Call<List<Event>> searchEvents(@Header("Authorization") String authHeader, @Query("query") String query, @Query("limit") int limit, @Query("page") int page);

    @GET("/api/v2/ids")
    Call<AppId> getAppId(@Header("Authorization") String authHeader, @Query("id_to_obfuscate") String serverUrl);

    @GET("/api/v2/me/features/active")
    Call<List<String>> check4QuestionsFeature(@Header("Authorization") String authHeader);


}
