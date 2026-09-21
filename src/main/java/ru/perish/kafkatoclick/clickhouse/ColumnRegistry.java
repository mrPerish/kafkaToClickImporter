package ru.perish.kafkatoclick.clickhouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Колонки таблицы: значение {@code @JsonProperty} поля DTO, иначе имя поля.
 */
@Component
public class ColumnRegistry {

    private final Map<Class<?>, List<Column>> columnsByType = new ConcurrentHashMap<>();

    public List<Column> columns(Class<? extends BillingData> type) {
        return columnsByType.computeIfAbsent(type, ColumnRegistry::introspect);
    }

    private static List<Column> introspect(Class<?> type) {
        List<Class<?>> hierarchy = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            hierarchy.add(0, current);
        }
        List<Column> columns = new ArrayList<>();
        for (Class<?> current : hierarchy) {
            for (Field field : current.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                field.setAccessible(true);
                JsonProperty annotation = field.getAnnotation(JsonProperty.class);
                String name = annotation == null || annotation.value().isEmpty() ? field.getName() : annotation.value();
                columns.add(new Column(name, field));
            }
        }
        return List.copyOf(columns);
    }

    public record Column(String name, Field field) {

        public Object value(Object target) {
            try {
                return field.get(target);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot read " + field, e);
            }
        }
    }
}
