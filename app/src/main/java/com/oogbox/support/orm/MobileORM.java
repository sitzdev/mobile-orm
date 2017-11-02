package com.oogbox.support.orm;

import com.oogbox.support.orm.helper.SQLiteHelper;
import com.oogbox.support.orm.listeners.MobileORMConfigListener;

public class MobileORM {

    /**
     * Initialize Mobile ORM with configuration. It must be call before any CRUD operation
     * with MobileORM
     *
     * @param config Mobile ORM Config implemented class
     */
    public static void init(Class<? extends MobileORMConfigListener> config) {
        SQLiteHelper.mobileORMConfigListener = config;
    }

}
