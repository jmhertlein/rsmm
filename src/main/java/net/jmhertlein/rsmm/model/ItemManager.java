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
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author joshua
 */
public class ItemManager {
    private final Connection conn;

    private final ObservableList<Item> cache;

    public ItemManager(Connection conn) throws SQLException {
        this.conn = conn;
        cache = FXCollections.observableArrayList();

        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item ORDER BY item_name ASC")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Item i = new Item(rs);
                    cache.add(i);
                }
            }
        }
    }

    public void setFavorite(Item i, boolean favorite)
    {
        i.setFavorite(favorite);
    }

    public void updateFavorite(Item i, boolean favorite) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("UPDATE Item SET favorite=? WHERE item_id=?")) {
            p.setInt(1, i.getId());
            p.setBoolean(2, favorite);
            p.executeUpdate();
        }
    }

    public Optional<Item> getItem(String name) {
        return cache.stream().filter(i -> i.getName().equals(name)).findFirst();
    }

    public Optional<Item> getItem(int id) {
        return cache.stream().filter(i -> i.getId() == id).findFirst();
    }

    public Optional<Integer> getLimitFor(String name) throws SQLException {
        Optional<Item> i = getItem(name);
        if (i.isPresent()) {
            return Optional.of(i.get().getBuyLimit());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Integer> getLimitFor(int id) throws SQLException {
        Optional<Item> i = getItem(id);
        if (i.isPresent()) {
            return Optional.of(i.get().getBuyLimit());
        } else {
            return Optional.empty();
        }
    }

    public ObservableList<Item> getItems() {
        return cache;
    }
}
