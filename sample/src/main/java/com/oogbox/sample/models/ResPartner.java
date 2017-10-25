package com.oogbox.sample.models;

import android.content.Context;

import com.oogbox.sample.orm.DBModel;
import com.oogbox.support.orm.annotation.DataModel;

@DataModel("res.partner")
public class ResPartner extends DBModel {

    public ResPartner(Context context) {
        super(context);
    }
}
