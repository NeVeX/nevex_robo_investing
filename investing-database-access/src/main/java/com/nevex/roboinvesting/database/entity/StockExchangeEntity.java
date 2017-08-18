package com.nevex.roboinvesting.database.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/8/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_exchanges")
public class StockExchangeEntity {

    @Id
    @Column(name = "id")
    private short id;
    @Column(name = "name")
    private String name;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockExchangeEntity that = (StockExchangeEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StockExchangeEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
