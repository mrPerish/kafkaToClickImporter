package ru.perish.kafkatoclick.clickhouse;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import ru.rtksoft.smev3.billing.dto.RequestRejectedBillingData;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClickHouseWriterTest {

    private static final String TABLE = "raw_smev3_non_business_res";
    private static final String EXPECTED_SQL = "INSERT INTO raw_smev3_non_business_res "
            + "(mid, d, s_mn, ct_vvs, ct_to_original_mid, ct_to_sender_mnemonic, ct_no_content_type, ct_code, ct_description) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
    private final ClickHouseWriter writer = new ClickHouseWriter(jdbcTemplate, new ColumnRegistry(), meterRegistry);

    @Test
    @SuppressWarnings("unchecked")
    void insertsColumnsNamedAfterJsonProperties() throws Exception {
        PreparedStatement statement = mock(PreparedStatement.class);
        when(jdbcTemplate.batchUpdate(eq(EXPECTED_SQL), any(List.class), anyInt(), any()))
                .thenAnswer(invocation -> {
                    List<RequestRejectedBillingData> batch = invocation.getArgument(1);
                    ParameterizedPreparedStatementSetter<RequestRejectedBillingData> setter = invocation.getArgument(3);
                    for (RequestRejectedBillingData row : batch) {
                        setter.setValues(statement, row);
                    }
                    return new int[0][0];
                });

        UUID messageId = UUID.randomUUID();
        RequestRejectedBillingData data = new RequestRejectedBillingData();
        data.setMessageId(messageId);
        data.setSenderMnemonic("SENDER");
        data.setCode("ERR");

        ClickHouseWriter.WriteResult result = writer.write(TABLE, RequestRejectedBillingData.class, List.of(data));

        verify(statement).setObject(1, messageId.toString());
        verify(statement).setObject(2, null);
        verify(statement).setObject(3, "SENDER");
        verify(statement).setObject(8, "ERR");
        assertThat(result.inserted()).isEqualTo(1);
        assertThat(result.failed()).isEmpty();
        assertThat(meterRegistry.counter("importer.rows.inserted",
                "table", TABLE, "type", "RequestRejectedBillingData").count()).isEqualTo(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void keepsGoodRowsWhenBatchFails() {
        when(jdbcTemplate.batchUpdate(eq(EXPECTED_SQL), any(List.class), anyInt(), any()))
                .thenThrow(new DataIntegrityViolationException("constraint"));
        when(jdbcTemplate.update(eq(EXPECTED_SQL), any(PreparedStatementSetter.class)))
                .thenReturn(1)
                .thenThrow(new DataIntegrityViolationException("constraint"))
                .thenReturn(1);

        List<RequestRejectedBillingData> rows = List.of(
                new RequestRejectedBillingData(),
                new RequestRejectedBillingData(),
                new RequestRejectedBillingData());

        ClickHouseWriter.WriteResult result = writer.write(TABLE, RequestRejectedBillingData.class, rows);

        assertThat(result.inserted()).isEqualTo(2);
        assertThat(result.failed()).containsExactly(rows.get(1));
        assertThat(meterRegistry.counter("importer.rows.failed",
                "table", TABLE, "type", "RequestRejectedBillingData").count()).isEqualTo(1);
    }

    @Test
    void skipsEmptyBatch() {
        assertThat(writer.write(TABLE, RequestRejectedBillingData.class, List.of()).inserted()).isZero();
        verifyNoInteractions(jdbcTemplate);
    }
}
