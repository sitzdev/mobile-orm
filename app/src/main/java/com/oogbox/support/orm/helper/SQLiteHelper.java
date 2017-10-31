package com.oogbox.support.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.listeners.ModelsListener;
import com.oogbox.support.orm.utils.MetaReader;

import java.util.HashMap;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHelper.class.getSimpleName();
    public static final String KEY_DATABASE_NAME = "DATABASE_NAME";
    public static final String KEY_DATABASE_VERSION = "DATABASE_VERSION";
    public static final java.lang.String KEY_DATABASE_DEBUG = "DATABASE_DEBUG";

    public static Class<? extends ModelsListener> modelsListener;

    private Context context;

    public SQLiteHelper(Context context) {
        super(context, MetaReader.getDatabaseName(context), null,
                MetaReader.getDatabaseVersion(context));
        this.context = context;
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
        // No processor. using listener
        if (modelsListener != null) {
            try {
                Log.v(TAG, "Database Name   : " + getDatabaseName());
                ModelsListener listener = modelsListener.newInstance();
                for (BaseModel model : listener.getModels(context)) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

}
