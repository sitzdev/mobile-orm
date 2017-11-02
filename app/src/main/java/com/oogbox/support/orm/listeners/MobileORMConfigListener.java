package com.oogbox.support.orm.listeners;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;

import java.util.List;

public interface MobileORMConfigListener {
    List<BaseModel> getModels(Context context);

    String getDatabaseName(Context context);

}
