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
import java.sql.Timestamp;

/**
 * @author joshua
 */
public class Quote {
    private final String itemName;
    private final Timestamp quoteTS;
    private final RSInteger bid, ask;

    public Quote(ResultSet rs) throws SQLException {
        this.itemName = rs.getString("item_name");
        this.quoteTS = rs.getTimestamp("quote_ts");
        this.bid = new RSInteger(rs.getInt("bid1"));
        this.ask = new RSInteger(rs.getInt("ask1"));
    }

    public Quote(String itemName, Timestamp quoteTS, RSInteger bid, RSInteger ask) {
        this.itemName = itemName;
        this.quoteTS = quoteTS;
        this.bid = bid;
        this.ask = ask;
    }

    public String getItemName() {
        return itemName;
    }

    public Timestamp getQuoteTS() {
        return quoteTS;
    }

    public RSInteger getBid() {
        return bid;
    }

    public RSInteger getAsk() {
        return ask;
    }
}
