package com.oogbox.support.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oogbox.support.orm.annotation.DataModel;
import com.oogbox.support.orm.data.ORecord;
import com.oogbox.support.orm.data.ORecordValue;
import com.oogbox.support.orm.data.RelationOperation;
import com.oogbox.support.orm.data.RelationValue;
import com.oogbox.support.orm.helper.M2MTable;
import com.oogbox.support.orm.helper.SQLBuilder;
import com.oogbox.support.orm.helper.SQLiteHelper;
import com.oogbox.support.orm.types.ODateTime;
import com.oogbox.support.orm.types.OInteger;
import com.oogbox.support.orm.types.OManyToMany;
import com.oogbox.support.orm.types.OManyToOne;
import com.oogbox.support.orm.types.OOneToMany;
import com.oogbox.support.orm.types.helper.OFieldType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        return select(null, null);
    }

    public List<ORecord> select(String selection, String[] args) {
        List<ORecord> records = new ArrayList<>();
        Cursor cr = select(null, selection, args,
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
        ContentValues values = value.toContentValues();

        HashMap<String, ORecordValue> m2oRelRecords = value.getM2ORelRecords();
        if (!m2oRelRecords.isEmpty()) {
            ORecordValue newValues = handleM2ORecords(m2oRelRecords);
            for (String key : newValues.keySet()) {
                // Updating content values with updated value of m2o
                values.put(key, newValues.getInt(key));
            }
        }

        newId = ((Long) db.insert(getTableName(), null, values)).intValue();

        HashMap<String, RelationValue> relationValue = value.getRelationValues();
        if (!relationValue.isEmpty()) {
            handleRelationRecords(relationValue, newId);
        }

        db.close();
        return newId;
    }

    public int update(ORecordValue value, int _id) {
        return update(value, "_id = ?", new String[]{_id + ""});
    }

    public int update(ORecordValue value, String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = value.toContentValues();

        HashMap<String, ORecordValue> m2oRecords = value.getM2ORelRecords();
        if (!m2oRecords.isEmpty()) {
            ORecordValue newValues = handleM2ORecords(m2oRecords);
            for (String key : newValues.keySet()) {
                values.put(key, newValues.getInt(key));
            }
        }

        count = db.update(getTableName(), values, selection, args);

        HashMap<String, RelationValue> relationValue = value.getRelationValues();
        if (!relationValue.isEmpty()) {
            handleRelationRecords(relationValue, getIds(selection, args));
        }

        db.close();
        return count;
    }

    public int delete(int _id) {
        return delete("_id = ? ", new String[]{_id + ""});
    }

    public int delete(String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        count = db.delete(getTableName(), selection, args);
        db.close();
        return count;
    }

    public Integer[] getIds(String selection, String[] args) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.query(getTableName(), new String[]{"_id"}, selection, args, null, null, null);
        if (cr.moveToFirst()) {
            do {
                ids.add(cr.getInt(0));
            } while (cr.moveToNext());
        }
        cr.close();
        db.close();
        return ids.toArray(new Integer[ids.size()]);
    }

    public BaseModel createModel(Class<? extends BaseModel> model) {
        try {
            Constructor constructor = model.getConstructor(Context.class);
            return (BaseModel) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private ORecordValue handleM2ORecords(HashMap<String, ORecordValue> values) {
        ORecordValue newRecordValue = new ORecordValue();
        for (String key : values.keySet()) {
            OFieldType column = getColumn(key);
            if (column != null && column instanceof OManyToOne) {
                BaseModel refModel = createModel(column.getRefModel());
                if (refModel != null) {
                    int refId = refModel.create(values.get(key));
                    newRecordValue.put(key, refId);
                }
            }
        }
        return newRecordValue;
    }

    // Handling relation records
    private void handleRelationRecords(HashMap<String, RelationValue> relationValueHashMap,
                                       Integer... recordIds) {
        for (String key : relationValueHashMap.keySet()) {
            RelationValue value = relationValueHashMap.get(key);
            OFieldType column = getColumn(key);
            // One to Many
            if (column instanceof OOneToMany) {
                manageO2MRelValue(column, value, recordIds);
            }

            // Many to Many
            if (column instanceof OManyToMany) {
                manageM2MRelValue(column, value, recordIds);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void manageO2MRelValue(OFieldType column, RelationValue value, Integer... recordIds) {
        BaseModel refModel = createModel(column.getRefModel());
        String refColumn = column.getRefColumn();
        for (RelationOperation key : value.getValues().keySet()) {
            for (int id : recordIds) {
                switch (key) {
                    case APPEND:
                        for (Object obj : value.getValues().get(key)) {
                            if (obj instanceof Integer) {
                                refModel.update(new ORecordValue()
                                        .add(refColumn, id), (Integer) obj);
                            }
                            if (obj instanceof ORecordValue) {
                                ORecordValue refRecord = (ORecordValue) obj;
                                refRecord.put(refColumn, id);
                                refModel.create(refRecord);
                            }
                        }
                        break;
                    case REMOVE:
                        for (Object obj : value.getValues().get(key)) {
                            if (obj instanceof Integer) {
                                refModel.delete((Integer) obj);
                            }
                        }
                        break;
                    case REPLACE:
                    case SET_NULL:

                        ORecordValue setNull = new ORecordValue();
                        setNull.put(refColumn, null);
                        refModel.update(setNull, refColumn + " = ?", new String[]{id + ""});

                        if (key == RelationOperation.REPLACE) {
                            for (Object obj : value.getValues().get(key)) {
                                if (obj instanceof Integer) {
                                    refModel.update(new ORecordValue()
                                            .add(refColumn, id), (Integer) obj);
                                }
                                if (obj instanceof ORecordValue) {
                                    ORecordValue refRecord = (ORecordValue) obj;
                                    refRecord.put(refColumn, id);
                                    refModel.create(refRecord);
                                }
                            }

                        }
                        break;
                }
            }
        }
    }

    private void manageM2MRelValue(OFieldType column, RelationValue value, Integer... recordIds) {
        M2MTable m2MTable = new M2MTable(getContext(), this, column);
        for (RelationOperation key : value.getValues().keySet()) {
            for (int id : recordIds) {
                switch (key) {
                    case APPEND:
                        for (Object obj : value.getValues().get(key)) {
                            if (obj instanceof Integer) {
                                m2MTable.insert(id, (Integer) obj);
                            }
                            if (obj instanceof ORecordValue) {
                                m2MTable.insert(id, (ORecordValue) obj);
                            }
                        }
                        break;
                    case REMOVE:
                        for (Object obj : value.getValues().get(key)) {
                            if (obj instanceof Integer) {
                                m2MTable.removeRelRecord((Integer) obj);
                            }
                        }
                        break;
                    case SET_NULL:
                        // No Option for M2M
                        break;
                    case REPLACE:
                        m2MTable.removeAllRelation(id);
                        if (key == RelationOperation.REPLACE) {
                            for (Object obj : value.getValues().get(key)) {
                                if (obj instanceof Integer) {
                                    m2MTable.insert(id, (Integer) obj);
                                }
                                if (obj instanceof ORecordValue) {
                                    m2MTable.insert(id, (ORecordValue) obj);
                                }
                            }

                        }
                        break;
                }
            }
        }
    }
}
