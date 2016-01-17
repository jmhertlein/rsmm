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
package net.jmhertlein.rsmm.controller.item;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.view.item.ItemManagerTableModel;

/**
 *
 * @author joshua
 */
public class AddItemAction extends AbstractAction {
    private final JDialog parent;
    private final ItemManager items;
    private final ItemManagerTableModel table;
    private final JTextField nameField, limitField;

    public AddItemAction(JDialog parent, ItemManager items, ItemManagerTableModel table, JTextField nameField, JTextField limitField) {
        super("Add Item");
        this.parent = parent;
        this.items = items;
        this.table = table;
        this.nameField = nameField;
        this.limitField = limitField;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String name = nameField.getText();
            if(name.isEmpty()) {
                showError("Invalid Name", "Name cannot be empty.");
                return;
            }

            if(items.getItem(name).isPresent()) {
                showError("Invalid Name", "There is already an item named " + name);
                return;
            }

            int buyLimit;
            try {
                buyLimit = Integer.parseInt(limitField.getText());
            } catch(NumberFormatException nfe) {
                showError("Invalid GE Limit", nfe.getMessage());
                return;
            }

            items.addItem(name, buyLimit);
            Optional<Item> item = items.getItem(name);
            if(item.isPresent()) {
                table.fireTableDataChanged();
                nameField.setText("");
                limitField.setText("");
            } else {
                showError("Unexpected Error", "Item " + name + " was added to DB but then was not able to be found.");
                return;
            }
        } catch(SQLException ex) {
            showError("Database Error", ex.getMessage());
        }
    }

    private void showError(String title, String msg) {
        JOptionPane.showMessageDialog(parent, msg, title, JOptionPane.ERROR_MESSAGE);
    }
}
