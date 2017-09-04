package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "data_loader_runs")
public class DataLoaderRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "start_timestamp", columnDefinition = "timestamptz")
    private OffsetDateTime startTimestamp;
    @Column(name = "end_timestamp", columnDefinition = "timestamptz")
    private OffsetDateTime endTimestamp;
    @Column(name = "records_processed")
    private int recordsProcessed;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OffsetDateTime getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(OffsetDateTime startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public OffsetDateTime getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(OffsetDateTime endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

    public void setRecordsProcessed(int recordsProcessed) {
        this.recordsProcessed = recordsProcessed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataLoaderRunEntity that = (DataLoaderRunEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataLoaderRunEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", recordsProcessed=" + recordsProcessed +
                '}';
    }
}
