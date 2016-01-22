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
import net.jmhertlein.rsmm.model.update.UpdateListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Supplier;

/**
 * @author joshua
 */
public class ItemManager extends UpdatableManager {
    private final Connection conn;

    public ItemManager(Connection conn) {
        this.conn = conn;
    }

    public void addItem(String name, int buyLimit) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Item VALUES(?,?)")) {
            p.setString(1, name);
            p.setInt(2, buyLimit);
            p.executeUpdate();
        }
    }

    public Set<Item> matchItem(String match) throws SQLException {
        Set<Item> ret = new HashSet<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item WHERE item_name LIKE ?")) {
            p.setString(1, "%" + match + "%");
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    ret.add(new Item(rs));
                }
            }
        }

        return ret;
    }

    public Optional<Item> getItem(String name) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item WHERE item_name=?")) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Item(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public Optional<Integer> getLimitFor(String name) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT ge_limit FROM Item WHERE item_name=?")) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("ge_limit"));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    public List<Item> getItems() throws SQLException {
        List<Item> ret = new ArrayList<>();
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item ORDER BY item_name ASC")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    ret.add(new Item(rs));
                }
            }
        }

        return ret;
    }

    public Optional<Item> getItem(int i) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item ORDER BY item_name ASC LIMIT 1 OFFSET ?")) {
            p.setInt(1, i);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() ? Optional.of(new Item(rs)) : Optional.empty();
            }
        }
    }

    public int countItems() throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("SELECT COUNT(*) AS count FROM Item")) {
            try (ResultSet rs = p.executeQuery()) {
                rs.next();
                return rs.getInt("count");
            }
        }
    }
}
