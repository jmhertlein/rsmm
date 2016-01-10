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

import java.awt.Frame;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTable;
import net.jmhertlein.rsmm.model.ItemManager;

/**
 *
 * @author joshua
 */
public class ItemManagerFrame extends JDialog {
    private final ItemManager items;

    private final JButton addItemButton;
    private final JTable itemTable;
    private final ItemManagerTableModel itemTableModel;

    public ItemManagerFrame(ItemManager items, Frame frame) {
        super(frame, "Item Manager");
        this.items = items;

        addItemButton = new JButton();
        itemTableModel = new ItemManagerTableModel();
        itemTable = new JTable(itemTableModel);

        setupUI();
    }

    private void setupUI() {

    }

}
