package com.nevex.roboinvesting.database.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "data_loader_errors")
public class DataLoaderErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "timestamp", columnDefinition = "timestamptz")
    private OffsetDateTime timestamp;

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataLoaderErrorEntity that = (DataLoaderErrorEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataLoaderErrorEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
