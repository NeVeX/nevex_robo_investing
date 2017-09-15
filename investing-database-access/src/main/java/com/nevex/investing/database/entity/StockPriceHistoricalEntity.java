package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices_historical",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ticker_id", "date"}))
public class StockPriceHistoricalEntity extends StockPriceBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StockPriceEntity{" +
                "id=" + id +
                super.toString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceHistoricalEntity that = (StockPriceHistoricalEntity) o;
        return Objects.equals(getTickerId(), that.getTickerId()) &&
                Objects.equals(getDate(), that.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTickerId(), getDate());
    }
}
