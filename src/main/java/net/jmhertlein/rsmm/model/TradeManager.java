package net.jmhertlein.rsmm.model;

import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joshua on 1/21/16.
 */
public class TradeManager extends UpdatableManager {
    private final Connection conn;

    public TradeManager(Connection conn) {
        this.conn = conn;
    }

    public int getBoughtVolumeInGELimitWindow(String itemName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(quantity) AS sum " +
                "FROM Turn NATURAL JOIN Trade " +
                "WHERE item_name=? " +
                "AND quantity > 0 " +
                "AND trade_ts BETWEEN now()-(interval '4 hours') AND now();")) {
            ps.setString(1, itemName);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("sum");
            }
        }
    }

    public Map<String, Integer> getOutstandingGELimits() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT item_name, SUM(quantity) AS sum " +
                "FROM Item NATURAL JOIN Turn NATURAL JOIN Trade " +
                "WHERE quantity > 0 AND trade_ts BETWEEN now()-(interval '4 hours') AND now() " +
                "GROUP BY item_name;")) {

            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Integer> ret = new HashMap<>();
                while (rs.next()) {
                    ret.put(rs.getString("item_name"), rs.getInt("sum"));
                }
                return ret;
            }
        }
    }

    public void addTrade(int turnId, int price, int quantity) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement(
                "INSERT INTO Trade(turn_id,price,quantity) VALUES(?,?,?)")) {
            p.setInt(1, turnId);
            p.setInt(2, price);
            p.setInt(3, quantity);
            p.executeUpdate();
        }
    }

    public List<Trade> getTrades(Turn turn) throws SQLException {
        List<Trade> ret = new ArrayList<>();
        try (PreparedStatement p = conn.prepareStatement(
                "SELECT * FROM Trade WHERe turn_id=? ORDER BY trade_ts ASC")) {
            p.setInt(1, turn.getTurnId());
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    ret.add(new Trade(turn, rs));
                }
            }
        }

        return ret;
    }

    public void bustTrade(Trade t) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("DELETE FROM Trade WHERE turn_id=? AND trade_ts=?")) {
            p.setInt(1, t.getTurn().getTurnId());
            p.setTimestamp(2, t.getTradeTime());
            p.executeUpdate();
        }
    }
}
