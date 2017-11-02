package com.oogbox.support.orm.core.data;

import android.content.ContentValues;

import com.oogbox.support.orm.core.helper.RecordWrapper;
import com.oogbox.support.orm.utils.ObjectByteUtils;

import java.io.IOException;
import java.util.HashMap;

public class ORecordValue extends RecordWrapper<ORecordValue> {

    private HashMap<String, RelationValue> relationValueMap = new HashMap<>();
    private HashMap<String, ORecordValue> m2oRecords = new HashMap<>();

    public ORecordValue add(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * Content Provider compatible content values from RecordValues
     *
     * @return ContentValues compatible to add relation values
     */
    public ContentValues toResolverContentValues() {
        ContentValues values = new ContentValues();
        for (String key : keySet()) {
            if (get(key) instanceof RelationValue || get(key) instanceof ORecordValue) {
                try {
                    values.put(key, ObjectByteUtils.objectToByte(get(key)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                values.put(key, getString(key));
            }
        }
        return values;
    }

    /**
     * Convert to SQLite content values from RecordValues, Use when not working with content provider
     *
     * @return ContentValues
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        for (String key : keySet()) {
            if (get(key) instanceof RelationValue) {
                relationValueMap.put(key, (RelationValue) get(key));
            } else if (get(key) instanceof ORecordValue) {
                m2oRecords.put(key, (ORecordValue) get(key));
            } else {
                values.put(key, getString(key));
            }
        }
        return values;
    }

    public HashMap<String, RelationValue> getRelationValues() {
        return relationValueMap;
    }

    public HashMap<String, ORecordValue> getM2ORelRecords() {
        return m2oRecords;
    }
}
