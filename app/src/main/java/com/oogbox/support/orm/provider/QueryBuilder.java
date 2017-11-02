package com.oogbox.support.orm.provider;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.oogbox.support.orm.DataResolver;

public class QueryBuilder {

    private DataResolver resolver;
    private String selection, sortOrder, groupBy, having;
    private String[] args;
    private String[] projections;
    private Integer[] ids = {};
    private int rowId = -1, limit = -1;

    public QueryBuilder(DataResolver resolver, String selection, String[] args) {
        this.resolver = resolver;
        this.selection = selection;
        this.args = args;
    }

    public QueryBuilder forId(int _id) {
        rowId = _id;
        return this;
    }

    public QueryBuilder forIds(Integer... ids) {
        this.ids = ids;
        return this;
    }

    public QueryBuilder columns(String... columns) {
        projections = columns;
        return this;
    }

    public QueryBuilder sortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    public QueryBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public QueryBuilder having(String having) {
        this.having = having;
        return this;
    }

    public QueryBuilder withLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public Cursor execute() {
        Uri.Builder builder = resolver.getUri().buildUpon();
        if (rowId > 0) {
            builder.appendPath(rowId + "");
        }
        if (groupBy != null) {
            builder.appendQueryParameter(ModelContentProvider.KEY_GROUP_BY, groupBy);
        }
        if (having != null) {
            builder.appendQueryParameter(ModelContentProvider.KEY_HAVING, having);
        }
        if (limit > 0) {
            builder.appendQueryParameter(ModelContentProvider.KEY_LIMIT, limit + "");
        }
        if (ids.length > 0) {
            selection = BaseColumns._ID + " in (" + TextUtils.join(",", ids) + ")";
        }
        return resolver.getContext().getContentResolver()
                .query(builder.build(), projections, selection, args, sortOrder);
    }
}
