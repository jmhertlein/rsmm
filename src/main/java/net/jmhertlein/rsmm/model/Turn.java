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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author joshua
 */
public class Turn {
    private final Connection conn;
    private final int turnId;
    private final String itemName;
    private final LocalDateTime open;
    private final Optional<LocalDateTime> close;

    public Turn(Connection conn, ResultSet rs) throws SQLException {
        this.conn = conn;
        this.turnId = rs.getInt("turn_id");
        this.itemName = rs.getString("item_name");
        this.open = rs.getTimestamp("open_ts").toLocalDateTime();
        this.close = Optional.ofNullable(rs.getTimestamp("close_ts"))
                .map(ts -> ts.toLocalDateTime());
    }

    public int getTurnId() {
        return turnId;
    }

    public String getItemName() {
        return itemName;
    }

    public LocalDateTime getOpen() {
        return open;
    }

    public Optional<LocalDateTime> getClose() {
        return close;
    }

    public boolean isFlat() throws SQLException {
        try(PreparedStatement p = conn.prepareStatement("SELECT SUM(quantity) AS sum_qty FROM Trade WHERE turn_id=?")) {
            p.setInt(1, turnId);
            try(ResultSet rs = p.executeQuery()) {
                rs.next();
                return rs.getInt("sum_qty") == 0;
            }
        }
    }

    public void addTrade(int price, int quantity) throws SQLException {
        try(PreparedStatement p = conn.prepareStatement(
                "INSERT INTO Trade turn_id,price,quantity VALUES(?,?,?)")) {
            p.setInt(1, turnId);
            p.setInt(2, price);
            p.setInt(3, quantity);
            p.executeUpdate();
        }
    }

    public List<Trade> getTrades() throws SQLException {
        List<Trade> ret = new ArrayList<>();
        try(PreparedStatement p = conn.prepareStatement(
                "SELECT * FROM Trade WHERe turn_id=? ORDER BY trade_ts ASC")) {
            p.setInt(1, turnId);
            try(ResultSet rs = p.executeQuery()) {
                while(rs.next()) {
                    ret.add(new Trade(this, rs));
                }
            }
        }

        return ret;
    }

}
