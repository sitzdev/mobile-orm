package com.oogbox.support.orm.core.types;

import com.oogbox.support.orm.core.types.helper.OFieldType;

public class OInteger extends OFieldType<OInteger> {

    public OInteger(String label) {
        super(label);
    }

    @Override
    public String fieldTypeString() {
        return "INTEGER";
    }
}
