package ru.perish.kafkatoclick.kafka;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter;
import ru.perish.kafkatoclick.clickhouse.ClickHouseWriter.WriteResult;
import ru.perish.kafkatoclick.config.ImporterProperties;
import ru.perish.kafkatoclick.config.ImporterProperties.Route;
import ru.rtksoft.smev3.billing.dto.BillingData;
import ru.rtksoft.smev3.billing.dto.RequestRejectedBillingData;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class BillingDataListenerTest {

    private static final String TABLE = "raw_smev3_non_business_res";

    private final ImporterProperties properties = new ImporterProperties("billing", "billing-dlt", Map.of(
            "RequestRejected", new Route(RequestRejectedBillingData.class, TABLE, null, Map.of())));
    private final ClickHouseWriter writer = mock(ClickHouseWriter.class);
    private final DeadLetterPublisher deadLetterPublisher = mock(DeadLetterPublisher.class);
    private final BillingDataListener listener = new BillingDataListener(
            new MessageRouter(JsonMapper.builder().build(), properties),
            writer,
            deadLetterPublisher,
            new SimpleMeterRegistry());

    @Test
    void deadLettersUnknownTypeAndWritesTheRest() {
        when(writer.write(eq(TABLE), any(), any())).thenReturn(new WriteResult(1, List.of()));
        ConsumerRecord<String, String> unknown = record("Nope", "{}");

        listener.onMessages(List.of(record("RequestRejected", "{\"ct_code\":\"ERR\"}"), unknown));

        verify(deadLetterPublisher).send(unknown, "unknown_type", "Unknown __TypeId__ Nope");
        verifyNoMoreInteractions(deadLetterPublisher);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<BillingData>> rows = ArgumentCaptor.forClass(List.class);
        verify(writer).write(eq(TABLE), eq(RequestRejectedBillingData.class), rows.capture());
        assertThat(rows.getValue()).hasSize(1);
    }

    @Test
    void deadLettersRowsRejectedByClickHouse() {
        ConsumerRecord<String, String> record = record("RequestRejected", "{\"ct_code\":\"ERR\"}");
        when(writer.write(eq(TABLE), any(), any())).thenAnswer(invocation -> {
            List<BillingData> rows = invocation.getArgument(2);
            return new WriteResult(0, rows);
        });

        listener.onMessages(List.of(record));

        verify(deadLetterPublisher).send(eq(record), eq("insert_error"), anyString());
    }

    private ConsumerRecord<String, String> record(String typeId, String payload) {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("billing", 0, 42L, null, payload);
        record.headers().add("__TypeId__", typeId.getBytes(StandardCharsets.UTF_8));
        return record;
    }
}
