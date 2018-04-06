package com.bitlove.fetlife.view.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.model.dataobject.wrapper.User;

import java.util.List;

public class temp {

    public void v() {

        final LiveData<List<User>> userData = FetLifeApplication.instance.fetLifeUserDatabase.userDao().getLastLoggedInUser();
        userData.observeForever(new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable List<User> users) {
                userData.removeObserver(this);
            }
        });

    }

}
