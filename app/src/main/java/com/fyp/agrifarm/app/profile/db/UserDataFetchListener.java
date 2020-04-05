package com.fyp.agrifarm.app.profile.db;

import com.fyp.agrifarm.app.profile.model.User;

public interface UserDataFetchListener {
    void onUserDataFetched(User user);
}
