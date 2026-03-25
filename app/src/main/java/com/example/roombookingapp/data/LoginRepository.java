package com.example.roombookingapp.data;

import com.example.roombookingapp.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;

    private LoggedInUser user = null;

    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
    }

    public void login(String username, String password, LoginCallback callback) {
        dataSource.login(username, password, new LoginCallback() {
            @Override
            public void onSuccess(LoggedInUser user) {
                setLoggedInUser(user);
                callback.onSuccess(user); // pass result back
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}