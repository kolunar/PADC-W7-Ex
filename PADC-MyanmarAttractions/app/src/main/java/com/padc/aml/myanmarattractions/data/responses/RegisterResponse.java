package com.padc.aml.myanmarattractions.data.responses;

import com.google.gson.annotations.SerializedName;

import com.padc.aml.myanmarattractions.data.vos.UserVO;

/**
 * Created by user on 7/17/2016.
 */
public class RegisterResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("login_user")
    private UserVO loginUser;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public UserVO getLoginUser() {
        return loginUser;
    }
}
