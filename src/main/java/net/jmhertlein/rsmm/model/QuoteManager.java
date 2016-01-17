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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author joshua
 */
public class QuoteManager {
    private final Connection conn;

    public QuoteManager(Connection conn) {
        this.conn = conn;
    }

    public void addQuote(String name, int bid, int ask) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Quote(item_name,bid1,ask1) VALUES(?,?,?)")) {
            p.setString(1, name);
            p.setInt(2, bid);
            p.setInt(3, ask);
            p.executeUpdate();
        }
    }

    public Optional<Quote> getLatestQuote(String item) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Quote WHERE item_name=? ORDER BY quote_ts DESC LIMIT 1")) {
            p.setString(1, item);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Quote(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public List<Quote> getLatestQuotes(String item) throws SQLException {
        List<Quote> quotes = new ArrayList<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Quote WHERE item_name=? ORDER BY quote_ts DESC LIMIT 5")) {
            p.setString(1, item);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    quotes.add(new Quote(rs));
                }
            }
        }
        Collections.reverse(quotes);
        System.out.println(quotes.size());
        return quotes;
    }
}
