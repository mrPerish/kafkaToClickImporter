package ru.perish.kafkatoclick.kafka;

import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.config.ImporterProperties;
import ru.perish.kafkatoclick.config.ImporterProperties.Route;
import ru.rtksoft.smev3.billing.dto.BillingData;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Разбирает сообщение по значению заголовка {@code __TypeId__} и выбирает целевую таблицу.
 */
@Component
public class MessageRouter {

    private final ObjectMapper objectMapper;
    private final ImporterProperties properties;

    public MessageRouter(ObjectMapper objectMapper, ImporterProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public RoutedMessage route(String typeId, String payload) {
        Route route = properties.routes().get(typeId);
        if (route == null) {
            throw new UnknownMessageTypeException(typeId);
        }
        JsonNode json = objectMapper.readTree(payload);
        BillingData data = objectMapper.treeToValue(json, route.type());
        return new RoutedMessage(table(route, json), route.type(), data);
    }

    private String table(Route route, JsonNode json) {
        if (route.discriminatorField() == null) {
            return route.table();
        }
        JsonNode discriminator = json.get(route.discriminatorField());
        String value = discriminator == null || discriminator.isNull() ? null : discriminator.asString();
        String table = value == null ? null : route.tablesByDiscriminator().get(value);
        if (table == null) {
            throw new UnknownDiscriminatorException(route.discriminatorField(), value);
        }
        return table;
    }

    public record RoutedMessage(String table, Class<? extends BillingData> type, BillingData data) {
    }
}
