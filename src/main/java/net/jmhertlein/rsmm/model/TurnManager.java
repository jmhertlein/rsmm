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

import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author joshua
 */
public class TurnManager extends UpdatableManager {
    private final Connection conn;

    public TurnManager(Connection conn) {
        this.conn = conn;
    }

    public Optional<Turn> getOpenTurn(String itemName) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE item_name=? AND close_ts IS NULL")) {
            p.setString(1, itemName);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Turn(conn, rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public void newTurn(String itemName) throws SQLException, DuplicateOpenTurnException {
        if (getOpenTurn(itemName).isPresent()) {
            throw new DuplicateOpenTurnException(itemName);
        }

        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Turn(item_name) VALUES(?)")) {
            p.setString(1, itemName);
            p.executeUpdate();
        }
    }

    public List<Turn> getOpenTurns() throws SQLException {
        List<Turn> turns = new ArrayList<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Turn WHERE close_ts IS NULL")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    turns.add(new Turn(conn, rs));
                }
            }
        }
        return turns;
    }

    public Optional<RSInteger> getTotalClosedProfit() throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT SUM(price * (quantity*-1)) AS total_closed FROM Trade NATURAL JOIN Turn WHERE close_ts IS NOT NULL;")) {
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new RSInteger(rs.getInt("total_closed")));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Optional<RSInteger> getClosedProfitForDay(LocalDate now) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT date_trunc('day', close_ts)::date AS day, SUM(price * (quantity*-1)) AS closed_profit " +
                "FROM Trade NATURAL JOIN Turn " +
                "WHERE close_ts BETWEEN date_trunc('day', ?::timestamp) AND (?::date+1)::timestamp " +
                "GROUP BY day;")) {
            p.setDate(1, Date.valueOf(now));
            p.setDate(2, Date.valueOf(now));
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new RSInteger(rs.getInt("closed_profit")));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public RSInteger getTotalOpenProfit(QuoteManager quotes) throws SQLException, NoQuoteException {
        int profit = 0;
        for (Turn t : getOpenTurns()) {
            try {
                profit += t.getOpenProfit(quotes).intValue();
            } catch (ArithmeticException ignore) {
            }
        }
        return new RSInteger(profit);
    }

    public RSInteger getOpenTurnClosedProfit() throws SQLException {
        int profit = 0;
        for (Turn t : getOpenTurns()) {
            try {
                profit += t.getClosedProfit().intValue();
            } catch (ArithmeticException ignore) {
            }
        }
        return new RSInteger(profit);
    }

    public RSInteger getTotalPositionCost(QuoteManager quotes) throws SQLException, NoQuoteException {
        int cost = 0;
        for (Turn t : getOpenTurns()) {
            try {
                cost += t.getPositionCost(quotes).intValue();
            } catch (ArithmeticException ignore) {
            }
        }
        return new RSInteger(cost);
    }
}
