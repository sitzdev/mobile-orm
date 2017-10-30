package com.oogbox.sample;

import android.content.Context;

import com.oogbox.sample.models.Mobiles;
import com.oogbox.sample.models.Users;
import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.listeners.ModelsListener;

import java.util.ArrayList;
import java.util.List;

public class ModelList implements ModelsListener {
    @Override
    public List<BaseModel> getModels(Context context) {
        List<BaseModel> models = new ArrayList<>();
        models.add(new Users(context));
        models.add(new Mobiles(context));
        return models;
    }
}
