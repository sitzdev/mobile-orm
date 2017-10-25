package com.oogbox.sample.models;

import android.content.Context;

import com.oogbox.sample.orm.DBModel;
import com.oogbox.support.orm.annotation.DataModel;

@DataModel("system.users")
public class Users extends DBModel {

    public Users(Context context) {
        super(context);
    }
}
