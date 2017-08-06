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
package cafe.josh.rsmm.model;


import cafe.josh.joshfx.Dialogs;
import cafe.josh.rsmm.model.update.QuoteListener;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * @author joshua
 */
public class QuoteManager {
    private final Connection conn;
    private final RSType rsType;

    private final Map<LocalDate, Map<Item, List<Quote>>> cache;

    private final List<QuoteListener> quoteListeners;

    public QuoteManager(Connection conn, RSType rsType) {
        this.conn = conn;
        this.rsType = rsType;
        cache = new HashMap<>();
        quoteListeners = new ArrayList<>();
    }

    public Quote addQuote(Item item, int bid, int ask) throws SQLException {
        Timestamp quoteTs = Timestamp.from(Instant.now());
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Quote(item_id,rs_type,quote_ts,bid1,ask1,synthetic) VALUES(?,?,?,?,?,?)")) {
            p.setInt(1, item.getId());
            p.setString(2, rsType.getEnumString());
            p.setTimestamp(3, quoteTs);
            p.setInt(4, bid);
            p.setInt(5, ask);
            p.setBoolean(6, false);
            p.executeUpdate();
        }

        List<Quote> quotesList = getQuotesFor(item, LocalDate.now());
        Quote ret = new Quote(item, quoteTs, bid, ask, false);
        quotesList.add(ret);

        quoteListeners.stream().forEach(l -> l.onQuote(ret));
        return ret;
    }

    public void addListener(QuoteListener l) {
        quoteListeners.add(l);
    }

    public Optional<Quote> getLatestQuote(Item item) throws SQLException {
        return getQuotesFor(item, LocalDate.now()).stream().max((l, r) -> l.getQuoteTS().compareTo(r.getQuoteTS()));
    }

    public void setQuoteSynthetic(Quote q) throws SQLException {
        q.syntheticProperty().set(!q.isSynthetic());
        boolean isSynthetic = q.isSynthetic();

        try (PreparedStatement ps = conn.prepareStatement("UPDATE Quote SET synthetic=? WHERE quote_ts=? AND item_id=? AND rs_type=?")) {
            ps.setBoolean(1, isSynthetic);
            ps.setTimestamp(2, q.getQuoteTS());
            ps.setInt(3, q.getItem().getId());
            ps.setString(4, rsType.getEnumString());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error: no rows updated for " + q.toString());
            }
        }

        quoteListeners.stream().forEach(l -> l.onMarkSynthetic(q));
    }

    public List<Quote> getQuotesFor(Item item, LocalDate date) throws SQLException {
        Map<Item, List<Quote>> itemsForDay = cache.get(date);
        if (itemsForDay == null) {
            itemsForDay = new HashMap<>();
            cache.put(date, itemsForDay);
        }

        List<Quote> quotes = itemsForDay.get(item);
        if (quotes == null) {
            quotes = new ArrayList<>();
            itemsForDay.put(item, quotes);

            System.out.println("Cache miss: Quotes for " + item.getName() + " for " + date.toString());
            try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Quote WHERE item_id=? AND quote_ts::date = ? AND rs_type=? ORDER BY quote_ts ASC")) {
                p.setInt(1, item.getId());
                p.setDate(2, Date.valueOf(date));
                p.setString(3, rsType.getEnumString());
                try (ResultSet rs = p.executeQuery()) {
                    while (rs.next()) {
                        quotes.add(new Quote(item, rs));
                    }
                }
            }
        }

        return quotes;
    }

    public void deleteQuote(Quote q) throws SQLException {
        List<Quote> quotes = getQuotesFor(q.getItem(), q.getQuoteTS().toLocalDateTime().toLocalDate());
        if(quotes.size() == 1 && quotes.get(0).equals(q))
        {
            Dialogs.showMessage("Error Deleting Quote", "Cannot Delete Last Quote of Day", "You always need at least 1 quote once one's been added for the day.");
            return;
        }

        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Quote WHERE quote_ts=? AND item_id=? AND rs_type=?")) {
            ps.setTimestamp(1, q.getQuoteTS());
            ps.setInt(2, q.getItem().getId());
            ps.setString(3, rsType.getEnumString());
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Error: no rows updated for " + q.toString());
            }
        }

        if(!quotes.remove(q))
        {
            Dialogs.showMessage("Error Deleting Quote", "Weirdness When Deleting Quote", "The DB update seems to have gone fine but deleting it from the in-memory cache did not actually delete anything.", q.toString());
        }

        quoteListeners.stream().forEach(l -> l.quoteDeleted(q));
    }
}
