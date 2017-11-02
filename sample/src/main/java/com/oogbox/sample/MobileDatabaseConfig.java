package com.oogbox.sample;

import android.content.Context;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.listeners.MobileORMConfigListener;

import java.util.ArrayList;
import java.util.List;

public class MobileDatabaseConfig implements MobileORMConfigListener {
    @Override
    public List<BaseModel> getModels(Context context) {
        List<BaseModel> models = new ArrayList<>();
        models.add(new Users(context));
        models.add(new Mobiles(context));
        return models;
    }

    @Override
    public String getDatabaseName(Context context) {
        return null;
    }
}
