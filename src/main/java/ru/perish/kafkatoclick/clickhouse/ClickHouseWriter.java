package ru.perish.kafkatoclick.clickhouse;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.clickhouse.ColumnRegistry.Column;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.util.List;

/**
 * Пакетная вставка DTO в таблицу ClickHouse.
 */
@Component
public class ClickHouseWriter {

    private final JdbcTemplate jdbcTemplate;
    private final ColumnRegistry columnRegistry;

    public ClickHouseWriter(JdbcTemplate jdbcTemplate, ColumnRegistry columnRegistry) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnRegistry = columnRegistry;
    }

    public void write(String table, Class<? extends BillingData> type, List<? extends BillingData> rows) {
        if (rows.isEmpty()) {
            return;
        }
        List<Column> columns = columnRegistry.columns(type);
        jdbcTemplate.batchUpdate(insertSql(table, columns), rows, rows.size(), (statement, row) -> {
            for (int i = 0; i < columns.size(); i++) {
                Object value = columns.get(i).value(row);
                statement.setObject(i + 1, value == null ? null : value.toString());
            }
        });
    }

    private String insertSql(String table, List<Column> columns) {
        String names = String.join(", ", columns.stream().map(Column::name).toList());
        String placeholders = String.join(", ", columns.stream().map(column -> "?").toList());
        return "INSERT INTO " + table + " (" + names + ") VALUES (" + placeholders + ")";
    }
}
