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
import javax.swing.*;

import net.jmhertlein.rsmm.controller.AddQuoteAction;
import net.jmhertlein.rsmm.controller.QuoteItemSelectedAction;
import net.jmhertlein.rsmm.controller.item.RefreshOnFocusListener;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;

/**
 * @author joshua
 */
public class QuotePanel extends JPanel {
    private final JTable recentQuotes;
    private final JComboBox<Item> itemChooser;
    private final JTextField bidField, askField;
    private final JButton addQuoteButton;

    public QuotePanel(ItemManager items, QuoteManager quotes) {
        itemChooser = new JComboBox<>();
        itemChooser.addFocusListener(new RefreshOnFocusListener<>(this, items, () -> itemChooser.removeAllItems(), item -> itemChooser.addItem(item)));
        bidField = new JTextField(10);
        askField = new JTextField(10);
        addQuoteButton = new JButton();
        RecentQuotesTableModel tableModel = new RecentQuotesTableModel(quotes);
        recentQuotes = new JTable(tableModel);
        itemChooser.addItemListener(new QuoteItemSelectedAction(quotes, itemChooser, tableModel));

        addQuoteButton.setAction(new AddQuoteAction(this, quotes, itemChooser, tableModel, bidField, askField));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.gridy = 0;
        c.gridwidth = 4;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(recentQuotes.getTableHeader(), c);
        c.gridy = 1;
        add(recentQuotes, c);

        c.weightx = 0.25;
        c.gridwidth = 1;
        c.gridy = 2;
        add(itemChooser, c);
        c.gridx = 1;
        add(bidField, c);
        c.gridx = 2;
        add(askField, c);
        c.gridx = 3;
        add(addQuoteButton, c);
    }
}
