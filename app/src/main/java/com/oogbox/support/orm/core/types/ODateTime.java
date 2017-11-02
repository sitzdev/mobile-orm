package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class ODateTime extends OFieldType<ODateTime> {

    public ODateTime(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "DATETIME";
    }
}
