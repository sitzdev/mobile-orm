package com.oogbox.support.orm;

import com.oogbox.support.orm.helper.SQLiteHelper;
import com.oogbox.support.orm.listeners.ModelsListener;

public class ModelRegistry {

    public static void bindOn(Class<? extends ModelsListener> modelsListener) {
        SQLiteHelper.modelsListener = modelsListener;
    }

}
