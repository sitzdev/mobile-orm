package com.oogbox.sample;

import android.app.Application;

import com.oogbox.support.orm.ModelRegistry;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ModelRegistry.bindOn(ModelList.class);
    }
}
