package com.oogbox.support.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.listeners.ModelsListener;
import com.oogbox.support.orm.utils.MetaReader;

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
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // No processor. using listener
        if (modelsListener != null) {
            try {
                Log.v(TAG, "Database Name   : " + getDatabaseName());
                ModelsListener listener = modelsListener.newInstance();
                for (BaseModel model : listener.getModels(context)) {
                    sqLiteDatabase.execSQL(model.getSQLBuilder().createStatement());
                    Log.v(TAG, "Table created " + model.getTableName());
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
