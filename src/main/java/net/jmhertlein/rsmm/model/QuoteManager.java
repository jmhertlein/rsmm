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
import javafx.collections.ObservableSet;
import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author joshua
 */
public class QuoteManager extends UpdatableManager {
    private final Connection conn;

    private final ObservableMap<Date, ObservableMap<Item, ObservableSet<Quote>>> cache;

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

        getQuotesFor(item, new Date(new java.util.Date().getTime())).add(new Quote(item.getName(), quoteTs, bid, ask));
    }

    public Optional<Quote> getLatestQuote(Item item) throws SQLException {
        return getQuotesFor(item, new Date(new java.util.Date().getTime())).stream().min((l, r) -> l.getQuoteTS().compareTo(r.getQuoteTS()));
    }

    public Set<Quote> getQuotesFor(Item item, Date date) throws SQLException {
        ObservableMap<Item, ObservableSet<Quote>> itemsForDay = cache.get(date);
        if(itemsForDay == null)
        {
            itemsForDay = FXCollections.observableHashMap();
            cache.put(date, itemsForDay);
        }

        ObservableSet<Quote> quotes = itemsForDay.get(item);
        if(quotes == null)
        {
            quotes = FXCollections.observableSet(new LinkedHashSet<Quote>());
            itemsForDay.put(item, quotes);

            try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Quote WHERE item_name=? AND quote_ts::date = ? ORDER BY quote_ts DESC")) {
                p.setString(1, item.getName());
                p.setDate(2, date);
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
