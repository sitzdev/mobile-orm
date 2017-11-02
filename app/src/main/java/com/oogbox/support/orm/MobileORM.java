package com.oogbox.support.orm;

import android.content.Context;

import com.oogbox.support.orm.core.helper.SQLiteHelper;
import com.oogbox.support.orm.core.listeners.MobileORMConfigListener;
import com.oogbox.support.orm.provider.ModelContentProvider;
import com.oogbox.support.orm.utils.MetaReader;

public class MobileORM {

    /**
     * Initialize Mobile ORM with configuration. It must be call before any CRUD operation
     * with MobileORM
     *
     * @param config Mobile ORM Config implemented class
     */
    public static void init(Context context, Class<? extends MobileORMConfigListener> config) {
        SQLiteHelper.mobileORMConfigListener = config;
        ModelContentProvider.DB_AUTHORITY = MetaReader.getDatabaseAuthority(context);
    }

}
