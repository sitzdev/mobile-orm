package com.oogbox.support.orm;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.data.ORecord;
import com.oogbox.support.orm.data.ORecordValue;
import com.oogbox.support.orm.helper.SQLBuilder;
import com.oogbox.support.orm.helper.SQLiteHelper;
import com.oogbox.support.orm.types.ODateTime;
import com.oogbox.support.orm.types.OInteger;
import com.oogbox.support.orm.types.helper.OFieldType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseModel extends SQLiteHelper {

    public BaseModel(Context context) {
        super(context);
    }

    private OInteger _id = new OInteger("ID").autoIncrement().primaryKey();
    private ODateTime _write_date = new ODateTime("Local Write Date");

    public String getModelName() {
        DataModel dataModel = getClass().getAnnotation(DataModel.class);
        if (dataModel != null) {
            return dataModel.value();
        }
        return getClass().getSimpleName().toLowerCase();
    }

    public String getTableName() {
        String modelName = getModelName();
        if (modelName != null) {
            return modelName.replaceAll("\\.", "_");
        }
        return null;
    }

    public OFieldType getColumn(String column) {
        try {
            Field field = getClass().getDeclaredField(column);
            field.setAccessible(true);
            OFieldType type = (OFieldType) field.get(this);
            type.setName(field.getName());
            return type;
        } catch (Exception e) {
            try {
                Field field = getClass().getSuperclass().getDeclaredField(column);
                field.setAccessible(true);
                OFieldType type = (OFieldType) field.get(this);
                type.setName(field.getName());
                return type;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }

    public List<OFieldType> getColumns() {
        List<OFieldType> columns = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
        for (Field field : fields) {
            if (field.getType().getSuperclass()
                    .isAssignableFrom(OFieldType.class)) {
                field.setAccessible(true);
                try {
                    OFieldType type = (OFieldType) field.get(this);
                    type.setName(field.getName());
                    columns.add(type);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return columns;
    }

    public SQLBuilder getSQLBuilder() {
        return new SQLBuilder(this);
    }

    public List<ORecord> select() {
        List<ORecord> records = new ArrayList<>();
        Cursor cr = select(null, null, null,
                null, null, null, null);
        if (cr.moveToFirst()) {
            do {
                ORecord record = ORecord.fromCursor(cr);
                record.setModel(this);
                records.add(record);
            } while (cr.moveToNext());
        }
        cr.close();
        return records;
    }

    public Cursor select(String[] columns, String selection, String[] args, String groupBy,
                         String having, String orderBy, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(getTableName(), columns, selection, args, groupBy, having, orderBy, limit);
    }

    public ORecord browse(int _id) {
        ORecord record = null;
        Cursor cr = select(null, "_id = ?", new String[]{_id + ""},
                null, null, null, null);
        if (cr.moveToFirst()) {
            record = ORecord.fromCursor(cr);
            record.setModel(this);
        }
        cr.close();
        return record;
    }

    public int create(ORecordValue value) {
        int newId;
        SQLiteDatabase db = getWritableDatabase();
        newId = ((Long) db.insert(getTableName(), null, value.toContentValues())).intValue();
        db.close();
        return newId;
    }

    public int update(ORecordValue value, int _id) {
        return update(value, "_id = ?", new String[]{_id + ""});
    }

    public int update(ORecordValue value, String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        count = db.update(getTableName(), value.toContentValues(), selection, args);
        db.close();
        return count;
    }

    public int delete(String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        count = db.delete(getTableName(), selection, args);
        db.close();
        return count;
    }

}
