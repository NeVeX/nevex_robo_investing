package com.nevex.investing.database.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created by Mark Cunningham on 8/7/2017.
 */
@Entity
@Table(schema = "investing", name = "stock_prices", uniqueConstraints = @UniqueConstraint(columnNames = "ticker_id"))
public class StockPriceEntity extends StockPriceBaseEntity implements MergeableEntity<StockPriceEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockPriceEntity that = (StockPriceEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "StockPriceEntity{" +
                "id=" + id +
                super.toString() +
                '}';
    }

    public void from(StockPriceBaseEntity entity) {
        this.merge(entity);
        this.setDate(entity.getDate());
    }

    @Override
    public void merge(StockPriceEntity other) {
        super.merge(other);
    }

    public static StockPriceEntity of(StockPriceHistoricalEntity historical) {
        StockPriceEntity price = new StockPriceEntity();
        price.from(historical);
        return price;
    }
}
