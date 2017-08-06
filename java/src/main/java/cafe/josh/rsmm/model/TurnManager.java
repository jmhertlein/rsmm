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

import cafe.josh.rsmm.model.update.TradeListener;
import cafe.josh.rsmm.model.update.TurnListener;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * @author joshua
 */
public class TurnManager {
    private final Connection conn;
    private final RSType rsType;

    private final List<Turn> openTurns, closedTurnsToday;

    private final List<TurnListener> turnListeners;
    private final List<TradeListener> tradeListeners;

    public TurnManager(Connection conn, ItemManager items, QuoteManager quotes, RSType rsType) throws SQLException, NoSuchItemException, NoQuoteException {
        this.conn = conn;
        this.rsType = rsType;
        this.openTurns = new ArrayList<>();
        this.closedTurnsToday = new ArrayList<>();
        this.turnListeners = new ArrayList<>();
        this.tradeListeners = new ArrayList<>();


        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE close_ts IS NULL")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    openTurns.add(new Turn(conn, tradeListeners, items, quotes, rs));
                }
            }
        }

        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE close_ts::date = now()::date")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    closedTurnsToday.add(new Turn(conn, tradeListeners, items, quotes, rs));
                }
            }
        }
    }

    public void addTurnListener(TurnListener l) {
        turnListeners.add(l);
    }

    public void addTradeListener(TradeListener l) {
        tradeListeners.add(l);
    }

    public Optional<Turn> getOpenTurn(String itemName) {
        return openTurns.stream().filter(t -> t.getItemName().equals(itemName)).findFirst();
    }

    public Optional<Turn> getOpenTurn(Item i) {
        return openTurns.stream().filter(t -> t.getItem().equals(i)).findFirst();
    }

    public Turn newTurn(Item item, QuoteManager quotes) throws SQLException, DuplicateOpenTurnException, NoSuchItemException, NoQuoteException {
        if (getOpenTurn(item.getName()).isPresent()) {
            throw new DuplicateOpenTurnException(item.getName());
        }

        Timestamp openTs = Timestamp.from(Instant.now());

        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Turn(item_id, open_ts) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, item.getId());
            p.setTimestamp(2, openTs);
            p.executeUpdate();
            try (ResultSet keys = p.getGeneratedKeys()) {
                keys.next();
                int id = keys.getInt(1);

                System.out.println("New turn has id " + id);
                Turn newTurn = new Turn(conn, tradeListeners, quotes, id, item, openTs, null);
                openTurns.add(newTurn);

                turnListeners.stream().forEach(l -> l.onTurnOpen(newTurn));
                return newTurn;
            }
        }
    }

    public List<Turn> getOpenTurns() {
        return openTurns;
    }

    public int getClosedProfitForDay() {
        int sum = 0;
        for (Turn t : closedTurnsToday) {
            sum += t.getClosedProfit().intValue();
        }

        return sum;
    }

    public int getTotalOpenProfit(QuoteManager quotes) throws NoQuoteException, NoSuchItemException, SQLException {
        int profit = 0;
        for (Turn t : getOpenTurns()) {
            profit += t.getOpenProfit(quotes).intValue();
        }
        return profit;
    }

    public int getOpenTurnClosedProfit() {
        int profit = 0;
        for (Turn t : getOpenTurns()) {
            try {
                profit += t.getClosedProfit().intValue();
            } catch (ArithmeticException ignore) {
            }
        }
        return profit;
    }

    public int getTotalPositionCost(QuoteManager quotes) throws SQLException, NoQuoteException, NoSuchItemException {
        int cost = 0;
        for (Turn t : getOpenTurns()) {
            cost += t.getPositionCost(quotes);
        }
        return cost;
    }

    public void closeTurn(long turnId) throws SQLException {
        Timestamp closeTs = Timestamp.from(Instant.now());
        try (PreparedStatement p = conn.prepareStatement("UPDATE Turn SET close_ts=? WHERE turn_id=?")) {
            p.setTimestamp(1, closeTs);
            p.setLong(2, turnId);
            int rows = p.executeUpdate();
            if (rows > 0) {
                Turn turn = openTurns.stream().filter(t -> t.getTurnId() == turnId).findFirst().orElseThrow(() -> new NoSuchElementException("No such open turn for id: " + turnId));
                turn.setClose(closeTs);
                closedTurnsToday.add(turn);
                openTurns.remove(turn);
                turnListeners.stream().forEach(l -> l.onTurnClose(turn));
            }
        }
    }
}
