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
import cafe.josh.rsmm.model.update.TradeListener;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author joshua
 */
public class Turn implements Comparable<Turn> {
    private final Connection conn;
    private final long turnId;
    private final ObjectProperty<Item> item;
    private final SimpleObjectProperty<Timestamp> open, close;
    private final Set<Trade> trades;

    private final IntegerProperty openProfit, closedProfit, positionCost, position;

    private final List<TradeListener>  tradeListeners;

    public Turn(Connection conn, List<TradeListener> listeners, ItemManager items, QuoteManager quotes, ResultSet rs) throws SQLException, NoSuchItemException, NoQuoteException {
        this(conn, listeners, quotes, rs.getLong("turn_id"), getItemFromResults(rs, items), rs.getTimestamp("open_ts"), rs.getTimestamp("close_ts"));
    }

    public ObjectProperty<Item> itemProperty() {
        return item;
    }

    public ObjectProperty<Timestamp> openProperty() {
        return open;
    }

    public ObjectProperty<Timestamp> closeProperty() {
        return close;
    }

    public IntegerProperty openProfitProperty() {
        return openProfit;
    }

    public IntegerProperty closedProfitProperty() {
        return closedProfit;
    }

    public IntegerProperty positionCostProperty() {
        return positionCost;
    }

    public IntegerProperty positionProperty() {
        return position;
    }

    private static Item getItemFromResults(ResultSet rs, ItemManager items) throws SQLException, NoSuchItemException {
        int itemId = rs.getInt("item_id");
        return items.getItem(itemId).orElseThrow(() -> new NoSuchItemException(itemId));
    }

    public Turn(Connection conn, List<TradeListener> listeners, QuoteManager quotes, long turnId, Item item, Timestamp open, Timestamp close) throws SQLException, NoQuoteException {
        this.conn = conn;
        this.turnId = turnId;
        this.item = new SimpleObjectProperty<>(item);
        this.open = new SimpleObjectProperty<>(open);
        this.close = new SimpleObjectProperty<>(close);
        this.tradeListeners = listeners;

        this.trades = new HashSet<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Trade WHERE turn_id=?")) {
            p.setLong(1, turnId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    trades.add(new Trade(this, rs));
                }
            }
        }

        openProfit = new SimpleIntegerProperty();
        closedProfit = new SimpleIntegerProperty();
        positionCost = new SimpleIntegerProperty();
        position = new SimpleIntegerProperty();
        recalculateProfit(quotes);
    }

    public void recalculateProfit(QuoteManager quotes) throws NoQuoteException, SQLException {
        openProfit.set(getOpenProfit(quotes).intValue());
        closedProfit.set(getClosedProfit().intValue());
        positionCost.set(getPositionCost(quotes));
        position.set(getPosition());
    }

    public Trade addTrade(int price, int quantity) throws SQLException {
        Trade trade = new Trade(this, Timestamp.from(Instant.now()), price, quantity);
        try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO Trade(turn_id,trade_ts,price,quantity) VALUES(?,?,?,?)")) {
            p.setLong(1, trade.getTurn().getTurnId());
            p.setTimestamp(2, trade.getTradeTime());
            p.setInt(3, trade.getPrice());
            p.setInt(4, trade.getQuantity());
            p.executeUpdate();
        }

        trades.add(trade);
        tradeListeners.stream().forEach(l -> l.onTrade(trade));
        return trade;
    }

    public void bustTrade(Trade t) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("DELETE FROM Trade WHERE turn_id=? AND trade_ts=?")) {
            p.setLong(1, t.getTurn().getTurnId());
            p.setTimestamp(2, t.getTradeTime());
            p.executeUpdate();
        }

        trades.remove(t);
        tradeListeners.stream().forEach(l -> l.onBust(t));
    }

    public long getTurnId() {
        return turnId;
    }

    public String getItemName() {
        return item.get().getName();
    }

    public Item getItem() {
        return item.get();
    }

    public Timestamp getOpen() {
        return open.get();
    }

    public Timestamp getClose() {
        return close.get();
    }

    public void setClose(Timestamp ts) {
        close.set(ts);
    }

    public boolean isFlat() {
        return getPosition() == 0;
    }

    public BigDecimal entryVWAP() {
        return vwapOf(trades.stream().filter(t -> t.getQuantity() > 0).collect(Collectors.toList()));
    }

    public BigDecimal exitVWAP() {
        return vwapOf(trades.stream().filter(t -> t.getQuantity() < 0).collect(Collectors.toList()));
    }

    private static BigDecimal vwapOf(List<Trade> trades) {
        int sumVolumeWeightedPrices = 0;
        int totalShares = 0;
        for (Trade t : trades) {
            sumVolumeWeightedPrices += t.getPrice() * Math.abs(t.getQuantity());
            totalShares += Math.abs(t.getQuantity());
        }

        BigDecimal numerator = new BigDecimal(sumVolumeWeightedPrices), denominator = new BigDecimal(totalShares);
        if (Objects.equals(denominator, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        } else {
            return numerator.divide(denominator, 4, BigDecimal.ROUND_HALF_EVEN);
        }
    }

    public int getPosition() {
        return trades.stream()
                .mapToInt(Trade::getQuantity)
                .sum();
    }

    public int getClosedPosition() {
        boolean isShort = getPosition() < 0;
        return trades.stream()
                .filter(t -> (t.getQuantity() > 0) == isShort)
                .mapToInt(Trade::getQuantity)
                .sum() * (isShort ? 1 : -1);
    }

    public BigDecimal getOpenProfit(QuoteManager quotes) throws NoQuoteException, SQLException {
        Quote quote = quotes.getLatestQuote(item.get()).orElseThrow(() -> new NoQuoteException(item.get().getName()));
        int pos = getPosition();

        if (pos > 0) {
            return new BigDecimal(pos * quote.getAsk()).subtract(entryVWAP().multiply(BigDecimal.valueOf(pos)));
        } else {
            pos = Math.abs(pos);
            return exitVWAP().subtract(BigDecimal.valueOf(quote.getBid())).multiply(BigDecimal.valueOf(pos));
        }
    }

    public BigDecimal getClosedProfit() {
        return exitVWAP().subtract(entryVWAP()).multiply(BigDecimal.valueOf(getClosedPosition()));
    }

    public int getPositionCost(QuoteManager quotes) throws SQLException, NoQuoteException {
        int pos = getPosition();
        int qty = Math.abs(pos);

        Quote quote = quotes.getLatestQuote(item.get()).orElseThrow(() -> new NoQuoteException(item.get().getName()));

        int multiplicand;
        if (pos > 0) {
            multiplicand = quote.getBid();
        } else {
            multiplicand = quote.getAsk();
        }

        return qty * multiplicand;
    }

    public Set<Trade> getTrades() {
        return trades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Turn turn = (Turn) o;

        return turnId == turn.turnId;

    }

    @Override
    public int hashCode() {
        return (int) (turnId ^ (turnId >>> 32));
    }

    @Override
    public int compareTo(Turn o) {
        return getOpen().compareTo(o.getOpen());
    }
}
