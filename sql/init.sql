CREATE TABLE IF NOT EXISTS events
(
    id String,
    source String,
    payload String,
    event_time DateTime64(3)
)
ENGINE = MergeTree
ORDER BY (event_time, id);
