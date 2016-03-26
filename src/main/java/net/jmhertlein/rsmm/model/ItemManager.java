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

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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

    private final ObservableMap<String, Item> cache;

    public ItemManager(Connection conn) throws SQLException {
        this.conn = conn;
        cache = FXCollections.observableHashMap();

        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item ORDER BY item_name ASC")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Item i = new Item(rs);
                    cache.put(i.getName(), i);
                }
            }
        }
    }

    public void addItem(String name, int buyLimit) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("INSERT INTO Item VALUES(?,?)")) {
            p.setString(1, name);
            p.setInt(2, buyLimit);
            p.executeUpdate();
        }

        cache.put(name, new Item(name, buyLimit));
    }

    public Optional<Item> getItem(String name) throws SQLException {
        return Optional.ofNullable(cache.get(name));
    }

    public Optional<Integer> getLimitFor(String name) throws SQLException {
        Optional<Item> i = getItem(name);
        if (i.isPresent()) {
            return Optional.of(i.get().getBuyLimit());
        } else {
            return Optional.empty();
        }
    }

    public Collection<Item> getItems() throws SQLException {
        return cache.values();
    }
}
