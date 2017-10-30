package com.oogbox.support.orm.types;

import com.oogbox.support.orm.types.helper.OFieldType;

public class OBlob extends OFieldType<OBlob> {

    public OBlob(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "BLOB";
    }
}
