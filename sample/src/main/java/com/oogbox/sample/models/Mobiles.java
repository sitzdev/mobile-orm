package com.oogbox.sample.models;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.types.OEnum;
import com.oogbox.support.orm.types.OVarchar;

@DataModel("user.mobiles")
public class Mobiles extends BaseModel {

    OVarchar name = new OVarchar("Device Name");
    OEnum device_type = new OEnum("Device Type")
            .addEnum("android", "Android")
            .addEnum("ios", "iOS");

    public Mobiles(Context context) {
        super(context);
    }
}
