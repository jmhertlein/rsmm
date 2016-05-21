/*
 * Copyright (C) 2016 joshua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jmhertlein.rsmm.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;

/**
 * @author joshua
 */
public class Trade {
    private final Turn turn;
    private final ReadOnlyObjectProperty<Timestamp> tradeTime;
    private final IntegerProperty price, quantity;


    public Trade(Turn parent, ResultSet rs) throws SQLException {
        this(parent, rs.getTimestamp("trade_ts"), rs.getInt("price"), rs.getInt("quantity"));
    }

    public Trade(Turn parent, Timestamp tradeTs, int price, int quantity) {
        this.turn = parent;
        this.tradeTime = new SimpleObjectProperty<>(tradeTs);
        this.price = new SimpleIntegerProperty(price);
        this.quantity = new SimpleIntegerProperty(quantity);
    }

    public ReadOnlyObjectProperty<Timestamp> tradeTimeProperty() {
        return tradeTime;
    }

    public IntegerProperty priceProperty() {
        return price;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public Turn getTurn() {
        return turn;
    }

    public Timestamp getTradeTime() {
        return tradeTime.get();
    }

    public int getPrice() {
        return price.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Trade trade = (Trade) o;

        if (!turn.equals(trade.turn)) return false;
        return tradeTime.equals(trade.tradeTime);

    }

    @Override
    public int hashCode() {
        int result = turn.hashCode();
        result = 31 * result + tradeTime.get().hashCode();
        return result;
    }
}
