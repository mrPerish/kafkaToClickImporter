package ru.perish.kafkatoclick.clickhouse;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.perish.kafkatoclick.clickhouse.ColumnRegistry.Column;
import ru.rtksoft.smev3.billing.dto.BillingData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Пакетная вставка DTO в таблицу ClickHouse.
 *
 * <p>Если батч не прошёл целиком (например, из-за ограничения на столбец), строки вставляются
 * по одной: сбойные возвращаются вызывающему, остальные всё равно попадают в таблицу.
 */
@Component
public class ClickHouseWriter {

    private static final Logger log = LoggerFactory.getLogger(ClickHouseWriter.class);

    private final JdbcTemplate jdbcTemplate;
    private final ColumnRegistry columnRegistry;
    private final MeterRegistry meterRegistry;

    public ClickHouseWriter(JdbcTemplate jdbcTemplate, ColumnRegistry columnRegistry, MeterRegistry meterRegistry) {
        this.jdbcTemplate = jdbcTemplate;
        this.columnRegistry = columnRegistry;
        this.meterRegistry = meterRegistry;
    }

    /**
     * @return количество вставленных строк и строки, которые вставить не удалось
     */
    public WriteResult write(String table, Class<? extends BillingData> type, List<? extends BillingData> rows) {
        if (rows.isEmpty()) {
            return new WriteResult(0, List.of());
        }
        Timer.Sample sample = Timer.start(meterRegistry);
        WriteResult result = insert(table, type, rows);
        sample.stop(meterRegistry.timer("importer.write", "table", table, "type", type.getSimpleName()));
        count("importer.rows.inserted", table, type, result.inserted());
        count("importer.rows.failed", table, type, result.failed().size());
        return result;
    }

    private WriteResult insert(String table, Class<? extends BillingData> type, List<? extends BillingData> rows) {
        List<Column> columns = columnRegistry.columns(type);
        String sql = insertSql(table, columns);
        try {
            jdbcTemplate.batchUpdate(sql, rows, rows.size(), (statement, row) -> bind(statement, columns, row));
            return new WriteResult(rows.size(), List.of());
        } catch (DataAccessException e) {
            log.warn("Batch insert into {} failed ({} rows), retrying row by row: {}",
                    table, rows.size(), e.getMessage());
            return insertOneByOne(sql, table, columns, rows);
        }
    }

    private WriteResult insertOneByOne(String sql, String table, List<Column> columns,
                                       List<? extends BillingData> rows) {
        int inserted = 0;
        List<BillingData> failed = new ArrayList<>();
        for (BillingData row : rows) {
            try {
                jdbcTemplate.update(sql, statement -> bind(statement, columns, row));
                inserted++;
            } catch (DataAccessException e) {
                failed.add(row);
                log.error("Row rejected by table {}: {}", table, values(columns, row), e);
            }
        }
        return new WriteResult(inserted, failed);
    }

    private void bind(PreparedStatement statement, List<Column> columns, BillingData row) throws SQLException {
        for (int i = 0; i < columns.size(); i++) {
            Object value = columns.get(i).value(row);
            statement.setObject(i + 1, value == null ? null : value.toString());
        }
    }

    private List<String> values(List<Column> columns, BillingData row) {
        List<String> values = new ArrayList<>(columns.size());
        for (Column column : columns) {
            values.add(column.name() + "=" + column.value(row));
        }
        return values;
    }

    private void count(String metric, String table, Class<? extends BillingData> type, int amount) {
        if (amount > 0) {
            meterRegistry.counter(metric, "table", table, "type", type.getSimpleName()).increment(amount);
        }
    }

    private String insertSql(String table, List<Column> columns) {
        String names = String.join(", ", columns.stream().map(Column::name).toList());
        String placeholders = String.join(", ", columns.stream().map(column -> "?").toList());
        return "INSERT INTO " + table + " (" + names + ") VALUES (" + placeholders + ")";
    }

    public record WriteResult(int inserted, List<BillingData> failed) {
    }
}
