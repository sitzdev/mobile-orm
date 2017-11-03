package com.oogbox.support.orm;

import com.oogbox.support.orm.core.helper.SQLiteHelper;
import com.oogbox.support.orm.core.helper.MobileORMConfig;
import com.oogbox.support.orm.provider.ModelContentProvider;
import com.oogbox.support.orm.utils.MetaReader;

public class MobileORM {

    /**
     * Initialize Mobile ORM with configuration. It must be call before any CRUD operation
     * with MobileORM
     *
     * @param config Mobile ORM Config
     */
    public static void init(MobileORMConfig config) {
        SQLiteHelper.setConfig(config);
        ModelContentProvider.DB_AUTHORITY = MetaReader.getDatabaseAuthority(config.getContext());
    }

}
