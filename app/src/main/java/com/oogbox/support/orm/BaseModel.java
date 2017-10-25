package com.oogbox.support.orm;

import android.content.Context;

import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.helper.SQLiteHelper;

public abstract class BaseModel extends SQLiteHelper {

    public BaseModel(Context context) {
        super(context);
    }


    public String getModelName() {
        DataModel dataModel = getClass().getAnnotation(DataModel.class);
        if (dataModel != null) {
            return dataModel.value();
        }
        return null;
    }

    public String getTableName() {
        String modelName = getModelName();
        if (modelName != null) {
            return modelName.replaceAll("\\.", "_");
        }
        return null;
    }
}
