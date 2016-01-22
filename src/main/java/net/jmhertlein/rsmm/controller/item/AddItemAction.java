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
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.jmhertlein.rsmm.model.ItemManager;

/**
 * @author joshua
 */
public class AddItemAction extends AbstractAction {
    private final JDialog parent;
    private final ItemManager items;
    private final JTextField nameField, limitField;

    public AddItemAction(JDialog parent, ItemManager items, JTextField nameField, JTextField limitField) {
        super("Add Item");
        this.parent = parent;
        this.items = items;
        this.nameField = nameField;
        this.limitField = limitField;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        try {
            String name = nameField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Name cannot be empty.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (items.getItem(name).isPresent()) {
                JOptionPane.showMessageDialog(parent, "There is already an item named " + name, "Invalid Name", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int buyLimit;
            try {
                buyLimit = Integer.parseInt(limitField.getText());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(parent, nfe.getMessage(), "Invalid GE Limit", JOptionPane.ERROR_MESSAGE);
                return;
            }

            items.addItem(name, buyLimit);
            items.fireUpdateEvent();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parent, ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
