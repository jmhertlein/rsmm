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

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author joshua
 */
public class Item implements Comparable<Item> {
    private final int itemId;
    private final ReadOnlyStringProperty name;
    private final ReadOnlyIntegerProperty buyLimit;
    private final BooleanProperty favorite;

    public Item(ResultSet rs) throws SQLException {
        this(rs.getInt("item_id"), rs.getString("item_name"), rs.getInt("ge_limit"), rs.getBoolean("favorite"));
    }

    public Item(int itemId, String name, int buyLimit, boolean favorite) {
        this.itemId = itemId;
        this.name = new SimpleStringProperty(name);
        this.buyLimit = new SimpleIntegerProperty(buyLimit);
        this.favorite = new SimpleBooleanProperty(favorite);
    }

    public String getName() {
        return name.get();
    }

    public int getBuyLimit() {
        return buyLimit.intValue();
    }

    public boolean isFavorite() {
        return favorite.get();
    }

    @Override
    public int compareTo(Item t) {
        return getName().compareTo(t.getName());
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return name.get().hashCode();
    }

    public void setFavorite(boolean favorite) {
        this.favorite.setValue(favorite);
    }

    public int getId() {
        return itemId;
    }
}
