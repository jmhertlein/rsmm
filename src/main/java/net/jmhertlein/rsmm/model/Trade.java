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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author joshua
 */
public class Trade {
    private final Turn turn;
    private final ReadOnlyObjectProperty<Timestamp> tradeTime;
    private final IntegerProperty price, quantity;

    public Trade(Turn parent, ResultSet rs) throws SQLException {
        this.turn = parent;
        this.tradeTime = new SimpleObjectProperty<>(rs.getTimestamp("trade_ts"));
        this.price = new SimpleIntegerProperty(rs.getInt("price"));
        this.quantity = new SimpleIntegerProperty(rs.getInt("quantity"));
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
}
