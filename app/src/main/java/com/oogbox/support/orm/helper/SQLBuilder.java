package com.oogbox.support.orm.helper;

import com.oogbox.support.orm.BaseModel;
import com.oogbox.support.orm.types.helper.OFieldType;

public class SQLBuilder {
    private BaseModel model;

    public SQLBuilder(BaseModel model) {
        this.model = model;
    }

    public String createStatement() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(model.getTableName());
        sql.append(" (");
        for (OFieldType field : model.getColumns()) {
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
            sql.append(",");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");
        return sql.toString();
    }
}
