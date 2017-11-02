package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class OBlob extends OFieldType<OBlob> {

    public OBlob(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "BLOB";
    }
}
