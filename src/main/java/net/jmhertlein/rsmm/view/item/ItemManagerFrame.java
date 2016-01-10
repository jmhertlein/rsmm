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

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import net.jmhertlein.rsmm.controller.item.AddItemAction;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;

/**
 *
 * @author joshua
 */
public class ItemManagerFrame extends JDialog {

    private final JTextField nameField, limitField;
    private final JButton addItemButton;
    private final JTable itemTable;
    private final JScrollPane tablePane;
    private final ItemManagerTableModel itemTableModel;

    public ItemManagerFrame(ItemManager items, Frame frame) {
        super(frame, "Item Manager");
        setModalityType(ModalityType.APPLICATION_MODAL);

        addItemButton = new JButton("Add Item");
        itemTableModel = new ItemManagerTableModel();
        itemTable = new JTable(itemTableModel);
        tablePane = new JScrollPane(itemTable);

        nameField = new JTextField();
        limitField = new JTextField();

        try {
            for(Item i : items.getItems()) {
                itemTableModel.addItem(i);
            }
        } catch(SQLException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "Database Exception", JOptionPane.ERROR_MESSAGE);
        }

        addItemButton.setAction(new AddItemAction(this, items, itemTableModel, nameField, limitField));

        setupComponents();
        setupUI();
        setSize(300, 300);
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        add(tablePane, c);

        c.gridwidth = 1;
        c.weighty = 0;
        c.weightx = 0.5;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(nameField, c);

        c.gridx = 1;
        add(limitField, c);

        c.weightx = 0;
        c.gridx = 2;
        add(addItemButton, c);
    }

    private void setupComponents() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        itemTable.setDefaultRenderer(Object.class, centerRenderer);
    }

    public static void main(String... args) {
        ItemManagerFrame f = new ItemManagerFrame(null, null);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
