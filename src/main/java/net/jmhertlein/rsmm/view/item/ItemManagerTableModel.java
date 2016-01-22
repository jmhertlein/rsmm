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
package net.jmhertlein.rsmm.view.item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.table.AbstractTableModel;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;

/**
 * @author joshua
 */
public class ItemManagerTableModel extends AbstractTableModel {
    private final List<Item> itemCache;

    public ItemManagerTableModel() {
        itemCache = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return itemCache.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Item";
            case 1:
                return "GE Buy Limit";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row < 0 || row > itemCache.size()) {
            return null;
        }

        Item item = itemCache.get(row);
        switch (col) {
            case 0:
                return item.getName();
            case 1:
                return item.getBuyLimit();
            default:
                return null;
        }
    }

    public void refresh(ItemManager items) throws SQLException {
        itemCache.clear();
        itemCache.addAll(items.getItems());
        fireTableDataChanged();
    }
}
