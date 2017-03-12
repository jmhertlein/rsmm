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

import cafe.josh.rsmm.model.update.ItemListener;

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

    private final List<Item> items;

    private final List<ItemListener> itemListeners;

    public ItemManager(Connection conn) throws SQLException {
        this.conn = conn;
        items = new ArrayList<>();
        itemListeners = new ArrayList<>();

        try (PreparedStatement p = conn.prepareStatement("SELECT * FROM Item ORDER BY item_name ASC")) {
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    Item i = new Item(rs);
                    items.add(i);
                }
            }
        }
    }

    public void setFavorite(Item i, boolean favorite) throws SQLException {
        i.setFavorite(favorite);
        updateFavorite(i, favorite);
        itemListeners.stream().forEach(l -> l.onItemFavorited(i));
    }

    public void addListener(ItemListener l) {
        itemListeners.add(l);
    }

    public void updateFavorite(Item i, boolean favorite) throws SQLException {
        try (PreparedStatement p = conn.prepareStatement("UPDATE Item SET favorite=? WHERE item_id=?")) {
            p.setBoolean(1, favorite);
            p.setInt(2, i.getId());
            p.executeUpdate();
        }
    }

    public Optional<Item> getItem(String name) {
        return items.stream().filter(i -> i.getName().equals(name)).findFirst();
    }

    public Optional<Item> getItem(int id) {
        return items.stream().filter(i -> i.getId() == id).findFirst();
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

    public List<Item> getItems() {
        return items;
    }
}
