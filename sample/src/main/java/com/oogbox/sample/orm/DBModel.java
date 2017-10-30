package com.oogbox.sample.orm;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.DataModels;

public class DBModel extends BaseModel {

    public DBModel(Context context) {
        super(context);
    }

    public Object getModel(String model) {
        return DataModels.get(getContext(), model);
    }
}
