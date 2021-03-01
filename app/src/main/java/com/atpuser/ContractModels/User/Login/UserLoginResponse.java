package com.atpuser.ContractModels.User.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLoginResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("user")
    @Expose
    private UserLogin user;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public UserLogin getUser() {
        return user;
    }

    public void setUser(UserLogin user) {
        this.user = user;
    }
}
