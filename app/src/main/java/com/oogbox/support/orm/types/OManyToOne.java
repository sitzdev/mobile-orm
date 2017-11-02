package com.oogbox.support.orm.types;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.types.helper.OFieldType;

public class OManyToOne extends OFieldType<OManyToOne> {

    public OManyToOne(String label, Class<? extends BaseModel> refModel) {
        super(label);
        setRefModel(refModel);
    }

    @Override
    public String fieldTypeString() {
        return "INTEGER";
    }
}
