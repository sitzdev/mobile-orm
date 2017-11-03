package com.oogbox.support.orm.core.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.utils.MetaReader;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();
    private static MobileORMConfig mobileORMConfig;
    public static final String PROCESSOR_CLASS_PATH = "com.oogbox.runtime.orm.MobileORMConfig";
    public static final String KEY_DATABASE_CONFIG = "DATABASE_CONFIG";
    public static final String KEY_DATABASE_NAME = "DATABASE_NAME";
    public static final String KEY_DATABASE_VERSION = "DATABASE_VERSION";
    public static final String KEY_DATABASE_DEBUG = "DATABASE_DEBUG";

    public static final String KEY_DATABASE_AUTHORITY = "DATABASE_AUTHORITY";

    private Context context;

    public SQLiteHelper(Context context) {
        super(context, MetaReader.getDatabaseName(context), null,
                MetaReader.getDatabaseVersion(context));
        this.context = context;
        if (mobileORMConfig == null) {
            // No mobile orm config called. finding processor generated config.
            try {
                Class<?> processorCls = Class.forName(PROCESSOR_CLASS_PATH);
                if (processorCls != null) {
                    Constructor constructor = processorCls.getConstructor(Context.class);
                    mobileORMConfig = (MobileORMConfig) constructor.newInstance(context);
                }
            } catch (Exception e) {
                // No config defined in processor, trying to get from manifest
                mobileORMConfig = MetaReader.getConfig(context);
            }
        }
    }

    public static void setConfig(MobileORMConfig config) {
        mobileORMConfig = config;
    }

    public static MobileORMConfig getConfig() {
        return mobileORMConfig;
    }

    public Context getContext() {
        return context;
    }

    public boolean debuggable() {
        return MetaReader.isDatabaseDebug(context);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Enabling Foreign key support
        if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (mobileORMConfig != null) {
            try {
                Log.v(TAG, "Database Name   : " + getDatabaseName());
                HashMap<String, BaseModel> models = mobileORMConfig.getModels();
                if (models != null) {
                    for (BaseModel model : models.values()) {
                        SQLBuilder builder = model.getSQLBuilder();
                        sqLiteDatabase.execSQL(builder.createStatement());
                        Log.v(TAG, "Table created " + model.getTableName());

                        // Creating m2m tables
                        HashMap<String, String> m2mStatements = builder.m2mStatements();
                        for (String key : m2mStatements.keySet()) {
                            sqLiteDatabase.execSQL(m2mStatements.get(key));
                            Log.v(TAG, "Table created " + key);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

}
