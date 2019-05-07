package com.bitlove.fetlife.legacy.model.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface FetLifeMultipartUploadApi {

    @Multipart
    @POST("/api/v2/me/pictures")
    Call<ResponseBody> uploadPicture(@Header("Authorization") String authHeader, @Part("picture\"; filename=\"android_app.png\" ") RequestBody picture, @Part("is_avatar") RequestBody isAvatar, @Part("only_friends") RequestBody friendsOnly, @Part("caption") RequestBody caption, @Part("is_of_or_by_user") RequestBody isFromUser);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

    @Multipart
    @PUT("/api/v2/me/videos/uploads/{video_upload_id}")
    Call<ResponseBody> uploadVideoPart(@Header("Authorization") String authHeader, @Path("video_upload_id") String videoUploadId, @Part("file\"; filename=\"android_app.part\" ") RequestBody video, @Part("number") RequestBody number);
    //TODO: solve dynamic file name
    //https://github.com/square/retrofit/issues/1063

}
