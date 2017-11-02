package com.oogbox.support.orm.data;

import android.content.ContentValues;

import com.oogbox.support.orm.helper.RecordWrapper;

import java.util.HashMap;

public class ORecordValue extends RecordWrapper<ORecordValue> {

    private HashMap<String, RelationValue> relationValueMap = new HashMap<>();
    private HashMap<String, ORecordValue> m2oRecords = new HashMap<>();

    public ORecordValue add(String key, Object value) {
        put(key, value);
        return this;
    }

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
