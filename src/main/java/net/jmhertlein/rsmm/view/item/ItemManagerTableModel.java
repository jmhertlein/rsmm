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
import java.util.Optional;
import javax.swing.table.AbstractTableModel;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;

/**
 *
 * @author joshua
 */
public class ItemManagerTableModel extends AbstractTableModel {
    private final ItemManager items;

    public ItemManagerTableModel(ItemManager items) {
        this.items = items;
    }

    @Override
    public int getRowCount() {
        try {
            return items.countItems();
        } catch(SQLException ex) {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int i) {
        switch(i) {
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
        Optional<Item> item;
        try {
            item = items.getItem(row);
        } catch(SQLException ex) {
            return null;
        }
        if(!item.isPresent()) {
            return null;
        }

        switch(col) {
            case 0:
                return item.get().getName();
            case 1:
                return item.get().getBuyLimit();
            default:
                return null;
        }
    }
}
