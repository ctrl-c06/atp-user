package com.atpuser.Contracts;

import android.database.Observable;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface IUserUpdateImage {
    @Multipart
    @POST("/api/person/update/profile")
    Call<ResponseBody> upload(
            @Part("person_id") RequestBody id,
            @Part MultipartBody.Part image
    );
}
