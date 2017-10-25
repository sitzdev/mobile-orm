package com.oogbox.sample.crm.models;

import android.content.Context;

import com.oogbox.sample.orm.DBModel;
import com.oogbox.support.orm.annotation.DataModel;

@DataModel("crm.lead")
public class CRMLead extends DBModel {

    public CRMLead(Context context) {
        super(context);
    }
}
