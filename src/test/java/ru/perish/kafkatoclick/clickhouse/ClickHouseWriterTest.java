package ru.perish.kafkatoclick.clickhouse;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import ru.rtksoft.smev3.billing.dto.RequestRejectedBillingData;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ClickHouseWriterTest {

    private static final String EXPECTED_SQL = "INSERT INTO raw_smev3_non_business_res "
            + "(mid, d, s_mn, ct_vvs, ct_to_original_mid, ct_to_sender_mnemonic, ct_no_content_type, ct_code, ct_description) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final ClickHouseWriter writer = new ClickHouseWriter(jdbcTemplate, new ColumnRegistry());

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

        writer.write("raw_smev3_non_business_res", RequestRejectedBillingData.class, List.of(data));

        verify(statement).setObject(1, messageId.toString());
        verify(statement).setObject(2, null);
        verify(statement).setObject(3, "SENDER");
        verify(statement).setObject(8, "ERR");
    }

    @Test
    void skipsEmptyBatch() {
        writer.write("raw_smev3_non_business_res", RequestRejectedBillingData.class, List.of());
        verifyNoInteractions(jdbcTemplate);
    }
}
