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

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author joshua
 */
public class Quote {
    private final Item item;
    private final ReadOnlyObjectProperty<Timestamp> quoteTS;
    private final IntegerProperty bid, ask;
    private final BooleanProperty synthetic;

    private final ReadOnlyIntegerProperty spread, profitPerLimit;

    public Quote(Item i, ResultSet rs) throws SQLException {
        this(
                i,
                rs.getTimestamp("quote_ts"),
                rs.getInt("bid1"),
                rs.getInt("ask1"),
                rs.getBoolean("synthetic"));
    }

    public Quote(Item i, Timestamp quoteTS, int bid, int ask, boolean synthetic) {
        this.item = i;
        this.quoteTS = new SimpleObjectProperty<>(quoteTS);
        this.bid = new SimpleIntegerProperty(bid);
        this.ask = new SimpleIntegerProperty(ask);
        this.synthetic = new SimpleBooleanProperty(synthetic);

        spread = new SimpleIntegerProperty(ask - bid);
        profitPerLimit = new SimpleIntegerProperty((ask - bid) * i.getBuyLimit());
    }


    public ReadOnlyObjectProperty<Timestamp> quoteTSProperty() {
        return quoteTS;
    }

    public IntegerProperty bidProperty() {
        return bid;
    }

    public IntegerProperty askProperty() {
        return ask;
    }

    public ReadOnlyIntegerProperty spreadProperty() {
        return spread;
    }

    public ReadOnlyIntegerProperty profitPerLimitProperty() {
        return profitPerLimit;
    }

    public BooleanProperty syntheticProperty() {
        return synthetic;
    }

    public boolean isSynthetic() {
        return synthetic.get();
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

    public Item getItem() {
        return item;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;

        if (!item.equals(quote.item)) return false;
        if (!quoteTS.equals(quote.quoteTS)) return false;
        if (!bid.equals(quote.bid)) return false;
        if (!ask.equals(quote.ask)) return false;
        return synthetic.equals(quote.synthetic);

    }

    @Override
    public int hashCode() {
        int result = item.hashCode();
        result = 31 * result + quoteTS.hashCode();
        result = 31 * result + bid.hashCode();
        result = 31 * result + ask.hashCode();
        result = 31 * result + synthetic.hashCode();
        return result;
    }
}
