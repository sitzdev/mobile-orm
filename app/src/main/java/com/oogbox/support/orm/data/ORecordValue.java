package com.oogbox.support.orm.data;

import android.content.ContentValues;

import com.oogbox.support.orm.helper.RecordWrapper;

public class ORecordValue extends RecordWrapper<ORecordValue> {


    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        for (String key : keySet()) {
            values.put(key, getString(key));
        }
        return values;
    }
}
