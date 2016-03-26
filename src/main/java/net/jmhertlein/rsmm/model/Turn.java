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

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author joshua
 */
public class Turn {
    private final int turnId;
    private final ObjectProperty<Item> item;
    private final SimpleObjectProperty<Timestamp> open, close;

    private final ReadOnlyIntegerProperty openProfit, closedProfit, positionCost, position;

    public Turn(ItemManager items, TradeManager trades, QuoteManager quotes, ResultSet rs) throws SQLException, NoSuchItemException, NoQuoteException {
        this.turnId = rs.getInt("turn_id");
        String itemName = rs.getString("item_name");
        this.item = new SimpleObjectProperty<>(items.getItem(itemName).orElseThrow(() -> new NoSuchItemException(itemName)));
        this.open = new SimpleObjectProperty<>(rs.getTimestamp("open_ts"));
        this.close = new SimpleObjectProperty<>(rs.getTimestamp("close_ts"));

        openProfit = new SimpleIntegerProperty(getOpenProfit(trades, quotes).intValue());
        closedProfit = new SimpleIntegerProperty(getClosedProfit(trades).intValue());
        positionCost = new SimpleIntegerProperty(getPositionCost(trades, quotes));
        position = new SimpleIntegerProperty(getPosition(trades));
    }

    public Turn(ItemManager items, TradeManager trades, QuoteManager quotes, int turnId, Item item, Timestamp open, Timestamp close) throws SQLException, NoQuoteException {
        this.turnId = turnId;
        this.item = new SimpleObjectProperty<>(item);
        this.open = new SimpleObjectProperty<>(open);
        this.close = new SimpleObjectProperty<>(close);

        openProfit = new SimpleIntegerProperty(getOpenProfit(trades, quotes).intValue());
        closedProfit = new SimpleIntegerProperty(getClosedProfit(trades).intValue());
        positionCost = new SimpleIntegerProperty(getPositionCost(trades, quotes));
        position = new SimpleIntegerProperty(getPosition(trades));
    }

    public int getTurnId() {
        return turnId;
    }

    public String getItemName() {
        return item.get().getName();
    }

    public Timestamp getOpen() {
        return open.get();
    }

    public Timestamp getClose() {
        return close.get();
    }

    public boolean isFlat(TradeManager trades) throws SQLException {
        return getPosition(trades) == 0;
    }

    public BigDecimal entryVWAP(TradeManager trades) throws SQLException {
        List<Trade> entries = trades.getTrades(this);
        return vwapOf(entries.stream().filter(t -> t.getQuantity() > 0).collect(Collectors.toList()));
    }

    public BigDecimal exitVWAP(TradeManager trades) throws SQLException {
        List<Trade> exits = trades.getTrades(this);
        return vwapOf(exits.stream().filter(t -> t.getQuantity() < 0).collect(Collectors.toList()));
    }

    private static BigDecimal vwapOf(List<Trade> trades) {
        int sumVolumeWeightedPrices = 0;
        int totalShares = 0;
        for (Trade t : trades) {
            sumVolumeWeightedPrices += t.getPrice() * t.getQuantity();
            totalShares += t.getQuantity();
        }

        BigDecimal numerator = new BigDecimal(sumVolumeWeightedPrices), denominator = new BigDecimal(totalShares);
        return numerator.divide(denominator, 4, BigDecimal.ROUND_HALF_EVEN);
    }

    public int getPosition(TradeManager trades) throws SQLException {
        return trades.getTrades(this)
                .stream()
                .mapToInt(Trade::getQuantity)
                .sum();
    }

    public int getClosedPosition(TradeManager trades) throws SQLException {
        boolean isShort = getPosition(trades) < 0;
        return trades.getTrades(this)
                .stream()
                .filter(t -> (t.getQuantity() > 0) == isShort)
                .mapToInt(Trade::getQuantity)
                .sum();
    }

    public BigDecimal getOpenProfit(TradeManager trades, QuoteManager quotes) throws SQLException, NoQuoteException {
        Quote quote = quotes.getLatestQuote(item.get().getName()).orElseThrow(() -> new NoQuoteException(item.get().getName()));
        int pos = getPosition(trades);

        if (pos > 0) {
            return new BigDecimal(pos * quote.getAsk()).subtract(entryVWAP(trades).multiply(BigDecimal.valueOf(pos)));
        } else {
            pos = Math.abs(pos);
            return exitVWAP(trades).subtract(BigDecimal.valueOf(quote.getBid())).multiply(BigDecimal.valueOf(pos));
        }
    }

    public BigDecimal getClosedProfit(TradeManager trades) throws SQLException {
        return exitVWAP(trades).subtract(entryVWAP(trades)).multiply(BigDecimal.valueOf(getClosedPosition(trades)));
    }

    public int getPositionCost(TradeManager trades, QuoteManager quotes) throws SQLException, NoQuoteException {
        int pos = getPosition(trades);
        int qty = Math.abs(pos);

        Quote quote = quotes.getLatestQuote(item.get().getName()).orElseThrow(() -> new NoQuoteException(item.get().getName()));

        int multiplicand;
        if (pos > 0) {
            multiplicand = quote.getBid();
        } else {
            multiplicand = quote.getAsk();
        }

        return qty * multiplicand;
    }
}
