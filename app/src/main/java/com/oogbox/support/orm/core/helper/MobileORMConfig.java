package com.oogbox.support.orm.core.helper;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class MobileORMConfig {

    private Context context;
    private List<Class<? extends BaseModel>> modelClasses = new ArrayList<>();

    public MobileORMConfig(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public final HashMap<String, BaseModel> getModels() throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException {
        HashMap<String, BaseModel> models = new HashMap<>();
        for (Class<? extends BaseModel> cls : modelClasses) {
            Constructor constructor = cls.getConstructor(Context.class);
            BaseModel model = (BaseModel) constructor.newInstance(getContext());
            models.put(model.getModelName(), model);
        }
        return models;
    }

    @SafeVarargs
    protected final void register(Class<? extends BaseModel>... models) {
        modelClasses.addAll(Arrays.asList(models));
    }

    public String getDatabaseName() {
        return null;
    }

    public abstract String authority();
}
