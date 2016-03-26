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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author joshua
 */
public class QuoteManager extends UpdatableManager {
    private final Connection conn;

    private final ObservableMap<Item, ObservableList<Quote>> cache;

    public QuoteManager(Connection conn) {
        this.conn = conn;
        cache = FXCollections.observableHashMap();
    }

    public void addQuote(Item item, int bid, int ask) throws SQLException {
        Timestamp quoteTs = Timestamp.from(Instant.now());
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Quote(item_name,quote_ts,bid1,ask1) VALUES(?,?,?,?)")) {
            p.setString(1, item.getName());
            p.setTimestamp(2, quoteTs);
            p.setInt(2, bid);
            p.setInt(3, ask);
            p.executeUpdate();
        }

        getLatestQuotes(item);
        cache.get(item).add(new Quote(item.getName(), quoteTs, bid, ask));
    }

    public Optional<Quote> getLatestQuote(Item item) throws SQLException {
        List<Quote> latestQuotes = getLatestQuotes(item);

        if(latestQuotes.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(latestQuotes.get(latestQuotes.size()-1));
        }
    }

    public List<Quote> getLatestQuotes(Item item) throws SQLException {
        ObservableList<Quote> quotes = cache.get(item);
        if(quotes == null)
        {
            quotes = FXCollections.observableArrayList();
            cache.put(item, quotes);

            try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Quote WHERE item_name=? ORDER BY quote_ts DESC LIMIT 5")) {
                p.setString(1, item.getName());
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        quotes.add(new Quote(rs));
                    }
                }
            }
        }

        return quotes;
    }
}
