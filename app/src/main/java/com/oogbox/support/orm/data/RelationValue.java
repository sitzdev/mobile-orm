package com.oogbox.support.orm.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RelationValue implements Serializable {

    private HashMap<RelationOperation, List<Object>> values = new HashMap<>();

    public RelationValue append(Object... objects) {
        manageRecord(RelationOperation.APPEND, objects);
        return this;
    }

    public RelationValue replace(Object... objects) {
        manageRecord(RelationOperation.REPLACE, objects);
        return this;
    }

    public RelationValue remove(Object... objects) {
        manageRecord(RelationOperation.REMOVE, objects);
        return this;
    }

    public RelationValue setNull(Object... objects) {
        manageRecord(RelationOperation.SET_NULL, objects);
        return this;
    }

    private void manageRecord(RelationOperation ope, Object... objects) {
        List<Object> opeObjects = new ArrayList<>();
        if (values.containsKey(ope)) {
            opeObjects.addAll(values.get(ope));
        }
        opeObjects.addAll(Arrays.asList(objects));
        values.put(ope, opeObjects);
    }

    public HashMap<RelationOperation, List<Object>> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
