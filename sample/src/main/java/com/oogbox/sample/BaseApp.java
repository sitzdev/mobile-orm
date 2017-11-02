package com.oogbox.sample;

import android.app.Application;

import com.oogbox.support.orm.MobileORM;

public class BaseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileORM.init(MobileDatabaseConfig.class);
    }
}
