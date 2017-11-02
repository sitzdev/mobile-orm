package com.oogbox.support.orm.types;

import com.oogbox.support.orm.types.helper.OFieldType;

public class ODateTime extends OFieldType<ODateTime> {

    public ODateTime(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "DATETIME";
    }
}
