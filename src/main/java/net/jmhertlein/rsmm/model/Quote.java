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

/**
 * @author joshua
 */
public class Quote {
    private final ReadOnlyStringProperty itemName;
    private final ReadOnlyObjectProperty<Timestamp> quoteTS;
    private final IntegerProperty bid, ask;

    public Quote(ResultSet rs) throws SQLException {
        this(
                rs.getString("item_name"),
                rs.getTimestamp("quote_ts"),
                rs.getInt("bid1"),
                rs.getInt("ask1"));
    }

    public Quote(String itemName, Timestamp quoteTS, int bid, int ask) {
        this.itemName = new SimpleStringProperty(itemName);
        this.quoteTS = new SimpleObjectProperty<>(quoteTS);
        this.bid = new SimpleIntegerProperty(bid);
        this.ask = new SimpleIntegerProperty(ask);
    }

    public String getItemName() {
        return itemName.get();
    }

    public Timestamp getQuoteTS() {
        return quoteTS.get();
    }

    public int getBid() {
        return bid.get();
    }

    public int getAsk() {
        return ask.get();
    }
}
