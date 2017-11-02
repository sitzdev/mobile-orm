package com.oogbox.support.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.oogbox.support.orm.core.data.ORecordValue;
import com.oogbox.support.orm.provider.ModelContentProvider;
import com.oogbox.support.orm.provider.QueryBuilder;

import java.lang.reflect.Constructor;

public class DataResolver {

    private Context context;
    private BaseModel model;

    private DataResolver(Context context, BaseModel model) {
        this.context = context;
        this.model = model;
    }

    private DataResolver(Context context, Class<? extends BaseModel> forModel) {
        this.context = context;
        try {
            Constructor constructor = forModel.getConstructor(Context.class);
            this.model = (BaseModel) constructor.newInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataResolver get(Context context, BaseModel model) {
        return new DataResolver(context, model);
    }

    public static DataResolver get(Context context, Class<? extends BaseModel> forModel) {
        return new DataResolver(context, forModel);
    }

    public static Uri buildURI(String authority, String path) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.appendPath(path);
        uriBuilder.scheme("content");
        return uriBuilder.build();
    }

    public Uri getUri() {
        return DataResolver.buildURI(getAuthority(), model.getModelName());
    }

    public String getAuthority() {
        return ModelContentProvider.DB_AUTHORITY;
    }

    public BaseModel getModel() {
        return model;
    }

    public Context getContext() {
        return context;
    }

    public QueryBuilder query() {
        return new QueryBuilder(this, null, null);
    }

    public QueryBuilder query(String selection, String... args) {
        return new QueryBuilder(this, selection, args);
    }

    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return context.getContentResolver()
                .query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public int insert(ORecordValue value) {
        ContentValues values = value.toResolverContentValues();
        Uri uri = context.getContentResolver().insert(getUri(), values);
        if (uri != null) return Integer.valueOf(uri.getLastPathSegment());
        return -1;
    }

    public int update(ORecordValue value, int _id) {
        ContentValues values = value.toResolverContentValues();
        return context.getContentResolver()
                .update(Uri.withAppendedPath(getUri(), _id + ""), values, null, null);
    }

    public int update(ORecordValue value, String selection, String[] args) {
        ContentValues values = value.toResolverContentValues();
        return context.getContentResolver().update(getUri(), values, selection, args);
    }

    public int delete(int _id) {
        return context.getContentResolver()
                .delete(Uri.withAppendedPath(getUri(), _id + ""), null, null);
    }

    public int delete(String selection, String[] args) {
        return context.getContentResolver().delete(getUri(), selection, args);
    }
}
