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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author joshua
 */
public class Item implements Comparable<Item> {
    private final String name;
    private final int buyLimit;

    public Item(ResultSet rs) throws SQLException {
        this.name = rs.getString("item_name");
        this.buyLimit = rs.getInt("ge_limit");
    }

    public Item(String name, int buyLimit) {
        this.name = name;
        this.buyLimit = buyLimit;
    }

    public String getName() {
        return name;
    }

    public int getBuyLimit() {
        return buyLimit;
    }

    @Override
    public int compareTo(Item t) {
        return getName().compareTo(t.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
