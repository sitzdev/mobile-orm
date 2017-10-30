package com.oogbox.sample.models;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.types.OVarchar;

@DataModel("sys.users")
public class Users extends BaseModel {

    OVarchar name = new OVarchar("Name");

    public Users(Context context) {
        super(context);
    }
}
