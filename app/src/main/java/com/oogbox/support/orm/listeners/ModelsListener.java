package com.oogbox.support.orm.listeners;

import android.content.Context;

import com.oogbox.support.orm.BaseModel;

import java.util.List;

public interface ModelsListener {
    List<BaseModel> getModels(Context context);
}
