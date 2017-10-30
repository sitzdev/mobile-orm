package com.oogbox.support.orm.data;

import android.database.Cursor;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.helper.RecordWrapper;
import com.oogbox.support.orm.types.OEnum;
import com.oogbox.support.orm.types.OManyToOne;
import com.oogbox.support.orm.types.OOneToMany;
import com.oogbox.support.orm.types.helper.OFieldType;

import java.util.Collections;
import java.util.List;

public class ORecord extends RecordWrapper<ORecord> {

    private BaseModel model;

    public ORecord setModel(BaseModel model) {
        this.model = model;
        return this;
    }

    public String getEnumValue(String key) {
        String value = getString(key);
        if (value != null && model != null) {
            OFieldType type = model.getColumn(key);
            if (type != null) {
                OEnum oEnum = (OEnum) type;
                return oEnum.getEnumVal(value);
            }
        }
        return value;
    }

    public ORecord readMany2One(String key) {
        Integer recordId = getInt(key);
        if (recordId != null && model != null) {
            OManyToOne m2o = (OManyToOne) model.getColumn(key);
            BaseModel refModel = model.createModel(m2o.getRefModel());
            if (refModel != null) {
                return refModel.browse(recordId);
            }
        }
        return null;
    }


    public List<ORecord> readOne2Many(String key) {
        if (model != null) {
            OOneToMany col = (OOneToMany) model.getColumn(key);
            Integer recordId = getInt("_id");
            if (recordId != null) {
                BaseModel refModel = model.createModel(col.getRefModel());
                return refModel.select(col.getRefColumn() + " = ? ", new String[]{recordId + ""});
            }
        }
        return Collections.emptyList();
    }

    public static ORecord fromCursor(Cursor cr) {
        ORecord record = new ORecord();
        for (String column : cr.getColumnNames()) {
            record.put(column, cursorValue(column, cr));
        }
        return record;
    }

    private static Object cursorValue(String column, Cursor cr) {
        Object value = false;
        int index = cr.getColumnIndex(column);
        switch (cr.getType(index)) {
            case Cursor.FIELD_TYPE_NULL:
                value = null;
                break;
            case Cursor.FIELD_TYPE_STRING:
                value = cr.getString(index);
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                value = cr.getInt(index);
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                value = cr.getFloat(index);
                break;
            case Cursor.FIELD_TYPE_BLOB:
                value = cr.getBlob(index);
                break;
        }
        return value;
    }

}
