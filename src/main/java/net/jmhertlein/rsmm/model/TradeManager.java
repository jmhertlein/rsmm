package net.jmhertlein.rsmm.model;

import net.jmhertlein.rsmm.model.update.UpdatableManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
}
