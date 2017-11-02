package com.oogbox.support.orm.core.helper;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.core.types.OManyToMany;
import com.oogbox.support.orm.core.types.OManyToOne;
import com.oogbox.support.orm.core.types.helper.OFieldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLBuilder {
    private BaseModel model;
    private List<OFieldType> many2Many = new ArrayList<>();

    public SQLBuilder(BaseModel model) {
        this.model = model;
    }

    public String createStatement() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(model.getTableName());
        sql.append(" (");
        for (OFieldType field : model.getColumns()) {
            if (field.fieldTypeString() != null) {
                sql.append("\n\t").append(field.getFieldName()).append(" ");
                sql.append(field.fieldTypeString());
                if (field.getSize() > 0) {
                    sql.append("(").append(field.getSize()).append(") ");
                }
                if (field.isPrimaryKey()) {
                    sql.append(" PRIMARY KEY ");
                }
                if (field.isAutoIncrement()) {
                    sql.append(" AUTOINCREMENT ");
                }

                if (field.getDefaultValue() != null) {
                    sql.append("DEFAULT ");
                    if (field.getDefaultValue() instanceof String)
                        sql.append("'").append(field.getDefaultValue()).append("'");
                    else
                        sql.append(field.getDefaultValue());
                }

                if (field instanceof OManyToOne) {
                    sql.append(" REFERENCES ").append(field.getRefModelTableName());
                    sql.append(" ON DELETE SET NULL ");
                }
                sql.append(",");
            }
            if (field instanceof OManyToMany) {
                many2Many.add(field);
            }
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");
        return sql.toString();
    }


    public HashMap<String, String> m2mStatements() {
        HashMap<String, String> m2mStatements = new HashMap<>();
        for (OFieldType column : many2Many) {
            M2MTable m2MTable = new M2MTable(model.getContext(), model, column);
            m2mStatements.put(m2MTable.getRelTableName(), m2MTable.createStatement());
        }
        return m2mStatements;
    }
}
