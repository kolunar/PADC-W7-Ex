package com.padc.aml.myanmarattractions.data.models;

import de.greenrobot.event.EventBus;
import com.padc.aml.myanmarattractions.data.vos.UserVO;
import com.padc.aml.myanmarattractions.events.DataEvent;
import com.padc.aml.myanmarattractions.events.UserEvent;

/**
 * Created by aung on 7/15/16.
 */
public class UserModel extends BaseModel {

    private static UserModel objInstance;

    private UserVO loginUser;

    private UserModel() {
        super();
    }

    public static UserModel getInstance() {
        if (objInstance == null) {
            objInstance = new UserModel();
        }
        return objInstance;
    }

    public void init(){
        loginUser = UserVO.loadLoginUser();

        if(loginUser != null){
            DataEvent.RefreshUserLoginStatusEvent event = new DataEvent.RefreshUserLoginStatusEvent();
            EventBus.getDefault().postSticky(event);
        }
    }

    public void logout() {
        loginUser.clearData();
        loginUser = null;

        DataEvent.RefreshUserLoginStatusEvent event = new DataEvent.RefreshUserLoginStatusEvent();
        EventBus.getDefault().post(event);
    }

    public UserVO getLoginUser(){
        return loginUser;
    }

    public boolean isUserLogin() {
        return loginUser != null;
    }

    public void register(String name, String email, String password, String dateOfBirth, String country) {
        dataAgent.register(name, email, password, dateOfBirth, country);
    }

    public void login(String email, String password) {
        dataAgent.login(email, password);
    }

    //Success Register :: inherited from BaseModel::EventBus
    public void onEventMainThread(UserEvent.SuccessRegistrationEvent event) {
        loginUser = event.getLoginUser();

        loginUser.saveLoginUser();
        // TO DO Persist login user object.
    }

    //Failed to Register
    public void onEventMainThread(UserEvent.FailedRegistrationEvent event) {
        //Do nothing on persistent layer.
    }

    //Success Login :: inherited from BaseModel::EventBus
    public void onEventMainThread(UserEvent.SuccessLoginEvent event) {
        loginUser = event.getLoginUser();

        loginUser.saveLoginUser();
        // TO DO Persist login user object.
    }

    //Failed to Register
    public void onEventMainThread(UserEvent.FailedLoginEvent event) {
        //Do nothing on persistent layer.
    }

}