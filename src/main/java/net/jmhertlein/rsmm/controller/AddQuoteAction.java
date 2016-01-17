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
package net.jmhertlein.rsmm.controller;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.RSInteger;
import net.jmhertlein.rsmm.view.quote.RecentQuotesTableModel;

/**
 * @author joshua
 */
public class AddQuoteAction extends AbstractAction {
    private final JComponent parent;
    private final QuoteManager quotes;
    private final JComboBox<Item> itemChooser;
    private final RecentQuotesTableModel tableModel;
    private final JTextField bidField, askField;

    public AddQuoteAction(JComponent parent, QuoteManager quotes, JComboBox<Item> itemChooser, RecentQuotesTableModel tableModel, JTextField bidField, JTextField askField) {
        super("Add Quote");
        this.parent = parent;
        this.quotes = quotes;
        this.itemChooser = itemChooser;
        this.tableModel = tableModel;
        this.bidField = bidField;
        this.askField = askField;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        RSInteger bid, ask;
        try {
            bid = RSInteger.parseInt(bidField.getText());
            ask = RSInteger.parseInt(askField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent, ex.getMessage(), "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!(bid.intValue() < ask.intValue())) {
            JOptionPane.showMessageDialog(parent, "Bid must be < ask", "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Item selected = (Item) itemChooser.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(parent, "No item selected.", "Invalid Item", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            quotes.addQuote(selected.getName(), bid.intValue(), ask.intValue());
            tableModel.showQuotesFor(selected.getName());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(parent, ex.getMessage(), "Error Adding Quote", JOptionPane.ERROR_MESSAGE);
        }
    }
}
