package net.kesgroom.cplt.utils;

import net.kesgroom.cplt.orm.Entity;
import net.kesgroom.cplt.orm.annotations.PrimaryKey;

import java.lang.reflect.Field;

public class KeyUtils {
    private static Field getPrimaryKeyField(Class<? extends Entity> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                return field;
            }
        }
        throw new IllegalArgumentException("La entidad " + entityClass.getSimpleName() + " no tiene @PrimaryKey");
    }
}
