package com.oogbox.support.orm.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oogbox.support.orm.utils.MetaReader;

public abstract class SQLiteHelper extends SQLiteOpenHelper {

    public static final String KEY_DATABASE_NAME = "DATABASE_NAME";
    public static final String KEY_DATABASE_VERSION = "DATABASE_VERSION";

    private Context context;

    public SQLiteHelper(Context context) {
        super(context, MetaReader.getDatabaseName(context), null,
                MetaReader.getDatabaseVersion(context));
        this.context = context;
    }


    public Context getContext() {
        return context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
