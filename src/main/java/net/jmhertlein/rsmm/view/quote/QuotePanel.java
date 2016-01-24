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
package net.jmhertlein.rsmm.view.quote;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.*;

import net.jmhertlein.rsmm.controller.AddQuoteAction;
import net.jmhertlein.rsmm.controller.QuoteItemSelectedAction;
import net.jmhertlein.rsmm.model.*;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

/**
 * @author joshua
 */
public class QuotePanel extends JPanel {
    private final JTable recentQuotes;
    private final RecentQuotesTableModel model;
    private final JComboBox<Item> itemChooser;
    private final JTextField bidField, askField;
    private final JButton addQuoteButton;

    public QuotePanel(ItemManager items, QuoteManager quotes) {
        itemChooser = new JComboBox<>();
        bidField = new JTextField(10);
        askField = new JTextField(10);
        addQuoteButton = new JButton();
        model = new RecentQuotesTableModel(quotes);
        recentQuotes = new JTable(model);
        itemChooser.addItemListener(new QuoteItemSelectedAction(quotes, itemChooser, model));

        try {
            refreshItems(items);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Items", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.gridy = 0;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(recentQuotes.getTableHeader(), c);
        c.gridy = 1;
        add(recentQuotes, c);

        c.weightx = 0;
        c.gridwidth = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        add(itemChooser, c);

        c.gridx = 1;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(bidField, c);
        c.gridx = 2;
        add(askField, c);

        c.gridx = 3;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        add(addQuoteButton, c);
    }

    public JComboBox<Item> getItemChooser() {
        return itemChooser;
    }

    public JTable getQuoteTable() {
        return recentQuotes;
    }

    public RecentQuotesTableModel getQuoteTableModel() {
        return model;
    }

    public void refreshItems(ItemManager items) throws SQLException {
        itemChooser.removeAllItems();
        for (Item q : items.getItems()) {
            itemChooser.addItem(q);
        }
    }

    public void reloadQuotes(QuoteManager quotes) {
        model.showQuotesFor(itemChooser.getItemAt(itemChooser.getSelectedIndex()).getName(), quotes);
    }

    public void setAddQuoteAction(QuoteManager quotes, TurnPanel turnPanel) {
        addQuoteButton.setAction(new AddQuoteAction(this, quotes, itemChooser, model, turnPanel, bidField, askField));
    }

    public Optional<Quote> getSelectedQuote() {
        return model.getQuoteAt(recentQuotes.getSelectedRow());
    }

    public void showQuotesFor(String itemName) {
        for (int i = 0; i < itemChooser.getItemCount(); i++) {
            if (itemChooser.getItemAt(i).getName().equals(itemName)) {
                itemChooser.setSelectedIndex(i);
                break;
            }
        }
    }
}
