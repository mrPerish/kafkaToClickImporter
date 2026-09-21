package ru.perish.kafkatoclick.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.util.Map;

@ConfigurationProperties(prefix = "importer")
public record ImporterProperties(String topic, Map<String, Route> routes) {

    /**
     * Маршрут для одного значения заголовка {@code __TypeId__}.
     *
     * @param type                   класс DTO, в который разбирается сообщение
     * @param table                  таблица ClickHouse по умолчанию
     * @param discriminatorField     json-поле, по значению которого выбирается таблица
     * @param tablesByDiscriminator  значение поля {@code discriminatorField} -> таблица
     */
    public record Route(Class<? extends BillingData> type,
                        String table,
                        String discriminatorField,
                        @DefaultValue Map<String, String> tablesByDiscriminator) {
    }
}
