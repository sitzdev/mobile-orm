package com.oogbox.support.orm.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.core.helper.SQLiteHelper;
import com.oogbox.support.orm.core.listeners.MobileORMConfigListener;
import com.oogbox.support.orm.utils.MetaReader;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

public abstract class ModelContentProvider extends ContentProvider {

    public static final String KEY_GROUP_BY = "groupBy";
    public static final String KEY_HAVING = "having";
    public static final String KEY_LIMIT = "dataLimit";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final HashMap<String, String> modelRegistry = new HashMap<>();

    public static String DB_AUTHORITY = null;

    private final int MATCH_COLLECTION = 901;
    private final int MATCH_SINGLE_ROW = 902;

    protected void setMatcher(String path, int identifier) {
        if (DB_AUTHORITY == null) {
            DB_AUTHORITY = MetaReader.getDatabaseAuthority(getContext());
        }
        uriMatcher.addURI(DB_AUTHORITY, path, identifier);
    }

    protected int match(Uri uri) {
        return uriMatcher.match(uri);
    }

    @Override
    @CallSuper
    public boolean onCreate() {
        bindModelMatcher();
        return true;
    }

    private void bindModelMatcher() {
        Class<?> configClass = SQLiteHelper.mobileORMConfigListener;
        if (configClass == null) {
            configClass = SQLiteHelper.mobileORMConfigListener = MetaReader.getConfigClass(getContext());
        }
        if (configClass != null) {
            try {
                MobileORMConfigListener config = (MobileORMConfigListener) configClass.newInstance();
                List<BaseModel> models = config.getModels(getContext());
                if (models != null) {
                    for (BaseModel model : config.getModels(getContext())) {
                        setMatcher(model.getModelName(), MATCH_COLLECTION);
                        setMatcher(model.getModelName() + "/#", MATCH_SINGLE_ROW);
                        modelRegistry.put(model.getModelName(), model.getClass().getCanonicalName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private BaseModel createModel(Uri uri) {
        if (uri.getPathSegments().size() > 0) {
            try {
                String modelName = uri.getPathSegments().get(0);
                if (modelRegistry.containsKey(modelName)) {
                    Constructor constructor = Class.forName(modelRegistry.get(modelName))
                            .getConstructor(Context.class);
                    return (BaseModel) constructor.newInstance(getContext());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cr = null;
        BaseModel model = createModel(uri);
        if (model != null) {
            int match = match(uri);
            switch (match) {
                case MATCH_COLLECTION:
                    String groupBy = uri.getQueryParameter(KEY_GROUP_BY);
                    String having = uri.getQueryParameter(KEY_HAVING);
                    String limit = uri.getQueryParameter(KEY_LIMIT);
                    cr = model.select(projection, selection, selectionArgs, groupBy, having,
                            sortOrder, limit);
                    break;
                case MATCH_SINGLE_ROW:
                    selection = BaseColumns._ID + " = ? ";
                    cr = model.select(projection, selection, new String[]{uri.getLastPathSegment()},
                            null, null, null, null);
                    break;
            }
        }
        if (cr != null && getContext() != null)
            cr.setNotificationUri(getContext().getContentResolver(), uri);
        return cr;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri resultUri = null;
        BaseModel model = createModel(uri);
        if (model != null) {
            int newId = model.create(model.createRecordValues(values));
            resultUri = uri.buildUpon().appendPath(newId + "").build();
            notifyDataChange(resultUri);
        }
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        BaseModel model = createModel(uri);
        int count = 0;
        if (model != null) {
            int match = match(uri);
            switch (match) {
                case MATCH_SINGLE_ROW:
                    selection = BaseColumns._ID + " = ?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                    break;
                case MATCH_COLLECTION:
                    // Pass
                    break;
                default:
                    return count;
            }
            count = model.delete(selection, selectionArgs);
            if (count > 0) notifyDataChange(uri);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count = 0;
        BaseModel model = createModel(uri);
        if (model != null) {
            int match = match(uri);
            switch (match) {
                case MATCH_COLLECTION:
                    // pass
                    break;
                case MATCH_SINGLE_ROW:
                    selection = BaseColumns._ID + " = ?";
                    selectionArgs = new String[]{uri.getLastPathSegment()};
                    break;
                default:
                    return 0;
            }
            count = model.update(model.createRecordValues(values), selection, selectionArgs);
            if (count > 0) notifyDataChange(uri);
        }
        return count;
    }

    protected void notifyDataChange(Uri uri) {
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null);
    }
}
