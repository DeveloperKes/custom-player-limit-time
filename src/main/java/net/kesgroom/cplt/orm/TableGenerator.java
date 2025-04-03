package net.kesgroom.cplt.orm;

import net.kesgroom.cplt.orm.annotations.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TableGenerator {
    public static String generateCreateTable(Class<? extends Entity> entityClass) throws IllegalAccessException {
        if (!entityClass.isAnnotationPresent(Table.class)) {
            throw new IllegalAccessException("This Class not is validate @Table");
        }

        String tableName = entityClass.getAnnotation(Table.class).name();
        if (tableName.isEmpty()) {
            tableName = entityClass.getSimpleName().toLowerCase();
        }

        List<String> columns = getStrings(entityClass);

        return String.format("CREATE TABLE IF NOT EXISTS %s (%s);", tableName, String.join(", ", columns));
    }

    private static @NotNull List<String> getStrings(Class<? extends Entity> entityClass) {
        List<String> columns = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(ManyToOne.class)) {
                Column column = field.getAnnotation(Column.class);
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                String columnType = getSqlType(field.getType());
                String constraints = (column.nullable() ? "" : "NOT NULL") + (column.unique() ? " UNIQUE" : "");
                columns.add(String.format("%s %s %s", columnName, columnType, constraints));
            } else if (field.isAnnotationPresent(PrimaryKey.class)) {
                PrimaryKey columnPrimaryKey = field.getAnnotation(PrimaryKey.class);
                String columnName = columnPrimaryKey.name().isEmpty() ? field.getName() : columnPrimaryKey.name();
                String columnType = (getSqlType(field.getType())) + " PRIMARY KEY";
                String constrains = (columnPrimaryKey.auto_increment() ? "IDENTITY(1,1)" : "");
                columns.add(String.format("%s %s %s", columnName, constrains, columnType));
            }
        }
        List<String> foreignKeys = new ArrayList<>();
        processRelations(entityClass, columns, foreignKeys);

        if (!foreignKeys.isEmpty()) {
            columns.add(String.join(", ", foreignKeys));
        }
        return columns;
    }

    private static String getSqlType(Class<?> type) {
        if (type == String.class) return "VARCHAR(255)";
        if (type == int.class || type == Integer.class) return "INT";
        if (type == Boolean.class) return "TINYINT";
        if (type == Date.class) return "DATETIME";
        return "TEXT";
    }

    private static void processRelations(Class<?> entityClass, List<String> columns, List<String> foreignKeys) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(OneToOne.class)) {
                String fkName = field.getAnnotation(OneToOne.class).foreignKey();
                if (fkName.isEmpty()) fkName = field.getName() + "_id";
                foreignKeys.add(String.format("FOREING KEY (%s) REFERENCES %s(id)", fkName, field.getType().getSimpleName().toLowerCase()));
            }
            if (field.isAnnotationPresent(ManyToOne.class)) {
                String fkName = field.getAnnotation(ManyToOne.class).foreignKey();
                if (fkName.isEmpty()) fkName = field.getName() + "_id";
                columns.add(String.format("%s %s", fkName, ""));
                foreignKeys.add("FOREIGN KEY (" + fkName + ") REFERENCES " +
                        field.getType().getSimpleName().toLowerCase() + "()");
            }
        }
    }
}
