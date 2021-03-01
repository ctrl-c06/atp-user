package com.atpuser.ContractModels.User.Login;

import com.google.gson.annotations.SerializedName;

public class UserLoginRequest {
    @SerializedName("phone_number")
    String phone_number;

    @SerializedName("username")
    String username;

    @SerializedName("mpin")
    String mpin;

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMpin(String mpin) {
        this.mpin = mpin;
    }
}
