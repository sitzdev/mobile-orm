package com.oogbox.support.orm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.oogbox.support.orm.helper.SQLiteHelper;
import com.oogbox.support.orm.listeners.MobileORMConfigListener;

public class MetaReader {

    public static Bundle getManifestMeta(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA
            );
            return info.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDatabaseName(Context context) {
        Bundle data = getManifestMeta(context);
        String defaultName = context.getPackageName().replaceAll("\\.", "_") + ".db";
        if (data != null) {
            return data.getString(SQLiteHelper.KEY_DATABASE_NAME, defaultName);
        }
        if (SQLiteHelper.mobileORMConfigListener != null) {
            try {
                MobileORMConfigListener config = SQLiteHelper.mobileORMConfigListener.newInstance();
                String dbName = config.getDatabaseName(context);
                if (dbName != null) {
                    return dbName;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultName;
    }

    public static Integer getDatabaseVersion(Context context) {
        Bundle data = getManifestMeta(context);
        if (data != null) {
            return data.getInt(SQLiteHelper.KEY_DATABASE_VERSION, 1);
        }
        return 1;
    }

    public static boolean isDatabaseDebug(Context context) {
        Bundle data = getManifestMeta(context);
        if (data != null) {
            return data.getBoolean(SQLiteHelper.KEY_DATABASE_DEBUG, false);
        }
        return false;
    }

}
