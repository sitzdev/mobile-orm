package com.oogbox.support.orm.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.oogbox.support.orm.core.helper.SQLiteHelper;
import com.oogbox.support.orm.core.helper.MobileORMConfig;

import java.lang.reflect.Constructor;

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

    public static MobileORMConfig getConfig(Context context) {
        Bundle data = getManifestMeta(context);
        if (data != null && data.containsKey(SQLiteHelper.KEY_DATABASE_CONFIG)) {
            try {
                Class<?> config = Class.forName(data.getString(SQLiteHelper.KEY_DATABASE_CONFIG));
                Constructor constructor = config.getConstructor(Context.class);
                return (MobileORMConfig) constructor.newInstance(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getDatabaseAuthority(Context context) {
        Bundle data = getManifestMeta(context);
        String defaultPath = context.getPackageName() + ".provider";
        if (data != null) {
            return data.getString(SQLiteHelper.KEY_DATABASE_AUTHORITY, defaultPath);
        }
        return defaultPath;
    }

    public static String getDatabaseName(Context context) {
        Bundle data = getManifestMeta(context);
        String defaultName = context.getPackageName().replaceAll("\\.", "_") + ".db";
        if (data != null && data.containsKey(SQLiteHelper.KEY_DATABASE_NAME)) {
            return data.getString(SQLiteHelper.KEY_DATABASE_NAME, defaultName);
        }
        MobileORMConfig config = SQLiteHelper.getConfig();
        if (config != null) {
            try {
                String dbName = config.getDatabaseName();
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
