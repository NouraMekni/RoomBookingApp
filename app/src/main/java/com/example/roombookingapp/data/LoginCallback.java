package com.example.roombookingapp.data;

import com.example.roombookingapp.data.model.LoggedInUser;

public interface LoginCallback {
    void onSuccess(LoggedInUser user);
    void onError(Exception e);
}