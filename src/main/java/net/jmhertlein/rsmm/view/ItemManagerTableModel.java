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
package net.jmhertlein.rsmm.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import net.jmhertlein.rsmm.model.Item;

/**
 *
 * @author joshua
 */
public class ItemManagerTableModel extends AbstractTableModel {
    private final List<Item> items;

    public ItemManagerTableModel() {
        items = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return items.size();
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
        if(row >= items.size()) {
            return null;
        }

        Item i = items.get(row);
        switch(col) {
            case 0:
                return i.getName();
            case 1:
                return i.getBuyLimit();
            default:
                return null;
        }
    }

}
