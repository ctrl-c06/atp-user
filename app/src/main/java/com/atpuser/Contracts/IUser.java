package com.atpuser.Contracts;

import com.atpuser.ContractModels.UserRegisterRequest;
import com.atpuser.ContractModels.UserRegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUser {
    @POST("/api/person/register")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest userRegisterRequest);

}
