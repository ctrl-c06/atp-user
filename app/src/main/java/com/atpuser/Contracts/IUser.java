package com.atpuser.Contracts;

import com.atpuser.ContractModels.User.Login.UserLoginRequest;
import com.atpuser.ContractModels.User.Login.UserLoginResponse;
import com.atpuser.ContractModels.User.Register.UserRegisterRequest;
import com.atpuser.ContractModels.User.Register.UserRegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IUser {
    @POST("/api/person/register")
    Call<UserRegisterResponse> register(@Body UserRegisterRequest userRegisterRequest);

    @POST("/api/person/login")
    Call<UserLoginResponse> login(@Body UserLoginRequest userLoginRequest);

}
