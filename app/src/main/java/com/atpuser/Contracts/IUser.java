package com.atpuser.Contracts;

import com.atpuser.ContractModels.User.UserRegisterRequest;
import com.atpuser.ContractModels.User.UserRegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUser {
    @POST("/api/person/register")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest userRegisterRequest);

}
