package com.oogbox.support.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oogbox.support.orm.core.annotation.DataModel;
import com.oogbox.support.orm.core.data.ORecord;
import com.oogbox.support.orm.core.data.ORecordValue;
import com.oogbox.support.orm.core.data.RelationOperation;
import com.oogbox.support.orm.core.data.RelationValue;
import com.oogbox.support.orm.core.helper.M2MTable;
import com.oogbox.support.orm.core.helper.SQLBuilder;
import com.oogbox.support.orm.core.helper.SQLiteHelper;
import com.oogbox.support.orm.core.types.ODateTime;
import com.oogbox.support.orm.core.types.OInteger;
import com.oogbox.support.orm.core.types.OManyToMany;
import com.oogbox.support.orm.core.types.OManyToOne;
import com.oogbox.support.orm.core.types.OOneToMany;
import com.oogbox.support.orm.core.types.helper.OFieldType;
import com.oogbox.support.orm.utils.OOGDateUtils;
import com.oogbox.support.orm.utils.ObjectByteUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class BaseModel extends SQLiteHelper {

    public static final String KEY_IGNORE_WRITE_DATE = "ignore_write_date";

    public BaseModel(Context context) {
        super(context);
    }

    /**
     * Default primary key column for every model with auto increment. Basically
     * used for maintain local relation and unique identification of record.
     */
    private OInteger _id = new OInteger("ID").autoIncrement().primaryKey();
    /**
     * Store last write_date of record when you update. Used to identify in sync mechanism
     */
    private ODateTime _write_date = new ODateTime("Local Write Date");

    /**
     * Get Model Name
     *
     * @return model name specified in @DataModel annotation or class simple name in lower case
     */
    public String getModelName() {
        DataModel dataModel = getClass().getAnnotation(DataModel.class);
        if (dataModel != null) {
            return dataModel.value();
        }
        return getClass().getSimpleName().toLowerCase();
    }

    /**
     * Get Table name generated from model name
     *
     * @return table name from model name
     */
    public String getTableName() {
        String modelName = getModelName();
        if (modelName != null) {
            return modelName.replaceAll("\\.", "_");
        }
        return null;
    }

    /**
     * Create @FieldType object with column name declared in model class.
     *
     * @param column name of the column to get from class
     * @return OFieldType object with column properties
     */
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

    /**
     * Gets all declared columns of the model
     *
     * @return List of columns
     */
    public List<OFieldType> getColumns() {
        List<OFieldType> columns = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        fields.addAll(getParentFields(getClass().getSuperclass()));
        fields.addAll(Arrays.asList(getClass().getDeclaredFields()));

        for (Field field : fields) {
            if (field.getType().getSuperclass()
                    .getCanonicalName().equals(OFieldType.class.getCanonicalName())) {
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

    private List<Field> getParentFields(Class cls) {
        List<Field> fields = new ArrayList<>();
        if (!cls.getCanonicalName().equals(BaseModel.class.getCanonicalName())) {
            fields.addAll(getParentFields(cls.getSuperclass()));
        }
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        return fields;
    }

    /**
     * Get the SQLBuilder for base statements (insert)
     *
     * @return SQLBuilder object with model binding
     */
    public SQLBuilder getSQLBuilder() {
        return new SQLBuilder(this);
    }

    /**
     * Select all records from data model (table)
     *
     * @return List of records (ORecord)
     */
    public List<ORecord> select() {
        return select(null, null);
    }

    /**
     * Select all record with selection filters
     *
     * @param selection Selection where clause
     * @param args      Arguments for selection
     * @return List of records fetched from data model (table)
     */
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

    /**
     * Select record based on where clause and other properties
     *
     * @param columns   Columns name to fetch
     * @param selection Where clause
     * @param args      Where clause arguments
     * @param groupBy   group by column
     * @param having    having clause
     * @param orderBy   order by column (ASC, DESC)
     * @param limit     limit for each request
     * @return Cursor object with fetched records
     */

    public Cursor select(String[] columns, String selection, String[] args, String groupBy,
                         String having, String orderBy, String limit) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(getTableName(), columns, selection, args, groupBy, having, orderBy, limit);
    }

    /**
     * Browse record based on primary key _id of record
     *
     * @param _id Unique id of record
     * @return Record object
     */
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

    /**
     * Create new record in the data model (table) with specified values
     * <p>
     * Here, when creating record in model it will check for relation records also
     * <p>
     * First many to one records are mapped with valid database id (if user passed new record object
     * it will insert record to related model first and than apply its new id to record to maintain local
     * relationship between records)
     * <p>
     * Also One to many and Many to Many records are handled after creating main record.
     *
     * @param value Values to store in data model
     * @return new created id
     */
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

    /**
     * Update record with updated values for _id
     *
     * @param value New updated values
     * @param _id   record id to update
     * @return count for updated records
     */
    public int update(ORecordValue value, int _id) {
        return update(value, "_id = ?", new String[]{_id + ""});
    }

    /**
     * Update multiple records with where clause and args
     * <p>
     * It will check for Many to one before updating any record to map valid ids for Many to one
     * <p>
     * Also it will check for One 2 many and many 2 many after updating main record to maintain proper
     * relation between records
     *
     * @param value     values to update in model
     * @param selection where clause to filter updating data
     * @param args      arguments for where clause
     * @return updated rows count
     */

    public int update(ORecordValue value, String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = value.toContentValues();
        value.put("_write_date", OOGDateUtils.getUTCDate());
        if (value.containsKey(KEY_IGNORE_WRITE_DATE)) {
            value.remove(KEY_IGNORE_WRITE_DATE);
            value.remove("_write_date");
        }

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

    /**
     * Delete specified record
     *
     * @param _id record id
     * @return counter of deleted records
     */
    public int delete(int _id) {
        return delete("_id = ? ", new String[]{_id + ""});
    }

    /**
     * Delete multiple records based on selection
     *
     * @param selection where clause
     * @param args      arguments for clause
     * @return number of record deleted
     */
    public int delete(String selection, String[] args) {
        int count;
        SQLiteDatabase db = getWritableDatabase();
        count = db.delete(getTableName(), selection, args);
        db.close();
        return count;
    }

    /**
     * Get all localIds based on selection
     *
     * @param selection selection where clause
     * @param args      arguments for selection where clause
     * @return Array of _ids
     */
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

    /**
     * Create model object from class path
     *
     * @param model model class path object
     * @return new object of model related to model class
     */
    public BaseModel createModel(Class<? extends BaseModel> model) {
        try {
            Constructor constructor = model.getConstructor(Context.class);
            return (BaseModel) constructor.newInstance(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Private
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

    /**
     * Get data resolver for handling Content Provider requests
     *
     * @return data resolver object
     */
    public DataResolver getResolver() {
        return DataResolver.get(getContext(), this);
    }

    /**
     * Responsible for generating ORecordValue from ContentValues. Used by ContentProvider
     *
     * @param values ContentValues from ContentProvider
     * @return insert ORecordValues with Many2Many, One2Many and Many2One object binding
     */
    public ORecordValue createRecordValues(ContentValues values) {
        ORecordValue recordValue = new ORecordValue();
        for (OFieldType column : getColumns()) {
            if (values.containsKey(column.getFieldName())) {
                Object value = values.get(column.getFieldName());
                if (column instanceof OManyToOne) {
                    if (value instanceof byte[]) {
                        try {
                            ORecordValue m2oValue = (ORecordValue)
                                    ObjectByteUtils.byteToObject((byte[]) value);
                            recordValue.put(column.getFieldName(), m2oValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (value instanceof Integer) {
                        recordValue.put(column.getFieldName(), value);
                    }
                } else if (column instanceof OOneToMany ||
                        column instanceof OManyToMany) {
                    try {
                        RelationValue relationValue =
                                (RelationValue) ObjectByteUtils.byteToObject((byte[]) value);
                        recordValue.put(column.getFieldName(), relationValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    recordValue.put(column.getFieldName(), values.get(column.getFieldName()));
                }
            }
        }
        return recordValue;
    }

    @Override
    public String toString() {
        return "Model(" + getModelName() + ")";
    }
}
