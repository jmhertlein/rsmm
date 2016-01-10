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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 *
 * @author joshua
 */
public class Trade {
    private final Turn turn;
    private final LocalDateTime tradeTime;
    private final int price, quantity;

    public Trade(Turn parent, ResultSet rs) throws SQLException {
        this.turn = parent;
        this.tradeTime = rs.getTimestamp("trade_ts").toLocalDateTime();
        this.price = rs.getInt("price");
        this.quantity = rs.getInt("quantity");
    }

    public Turn getTurn() {
        return turn;
    }

    public LocalDateTime getTradeTime() {
        return tradeTime;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

}
