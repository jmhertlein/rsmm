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

import java.awt.*;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.*;

import net.jmhertlein.rsmm.controller.AddQuoteAction;
import net.jmhertlein.rsmm.controller.QuoteItemSelectedAction;
import net.jmhertlein.rsmm.model.*;
import net.jmhertlein.rsmm.view.ScalableJTable;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

/**
 * @author joshua
 */
public class QuotePanel extends JPanel {
    private final JLabel maxGELimitSummaryLabel, usedGELimitLabel;
    private final JTable recentQuotes;
    private final RecentQuotesTableModel model;
    private final JComboBox<Item> itemChooser;
    private final JTextField bidField, askField;
    private final JButton addQuoteButton, syncQuoteButton;

    public QuotePanel(ItemManager items, QuoteManager quotes, TradeManager trades) {
        itemChooser = new JComboBox<>();
        bidField = new JTextField(10);
        askField = new JTextField(10);
        addQuoteButton = new JButton();
        model = new RecentQuotesTableModel(quotes);
        recentQuotes = new ScalableJTable(model);
        itemChooser.addItemListener(new QuoteItemSelectedAction(quotes, items, trades, this));
        syncQuoteButton = new JButton();
        maxGELimitSummaryLabel = new JLabel();
        usedGELimitLabel = new JLabel();

        try {
            refreshItems(items);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Items", JOptionPane.ERROR_MESSAGE);
        }

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = -1;

        c.gridy++;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        add(recentQuotes.getTableHeader(), c);
        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        add(recentQuotes, c);

        c.weightx = 0;
        c.weighty = 0;
        c.gridwidth = 1;
        c.gridy++;
        c.fill = GridBagConstraints.NONE;
        add(syncQuoteButton, c);

        c.gridx = 1;
        add(itemChooser, c);

        c.gridx = 2;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(bidField, c);
        c.gridx = 3;
        add(askField, c);

        c.gridx = 4;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        add(addQuoteButton, c);

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        {
            JPanel geLimitPanel = new JPanel();
            geLimitPanel.setLayout(new BoxLayout(geLimitPanel, BoxLayout.X_AXIS));
            geLimitPanel.add(Box.createHorizontalGlue());
            geLimitPanel.add(new JLabel("GE Limit Usage: "));
            geLimitPanel.add(usedGELimitLabel);
            geLimitPanel.add(new JLabel("/"));
            geLimitPanel.add(maxGELimitSummaryLabel);
            geLimitPanel.add(Box.createHorizontalGlue());

            add(geLimitPanel, c);
        }
    }

    public void refreshItems(ItemManager items) throws SQLException {
        itemChooser.removeAllItems();
        for (Item q : items.getItems()) {
            itemChooser.addItem(q);
        }
    }

    public void reloadQuotes(QuoteManager quotes, ItemManager items) {
        model.showQuotesFor(itemChooser.getItemAt(itemChooser.getSelectedIndex()).getName(), quotes, items);
    }

    public void setAddQuoteAction(QuoteManager quotes, TurnPanel turnPanel) {
        addQuoteButton.setAction(new AddQuoteAction(this, quotes, itemChooser, model, turnPanel, bidField, askField));
    }

    public void setSyncQuoteAction(AbstractAction a) {
        syncQuoteButton.setAction(a);
    }

    public Optional<Quote> getSelectedQuote() {
        return model.getQuoteAt(recentQuotes.getSelectedRow());
    }

    public void showQuotesFor(String name, QuoteManager quotes, ItemManager items, TradeManager trades) throws SQLException {
        int used = trades.getBoughtVolumeInGELimitWindow(name);
        int max = items.getLimitFor(name).get();
        usedGELimitLabel.setText(String.valueOf(used));
        maxGELimitSummaryLabel.setText(Integer.toString(max));

        float pctUsed = ((float) used) / max * 100;
        if(pctUsed > 90) {
            usedGELimitLabel.setForeground(Color.RED);
        } else if(pctUsed > 50) {
            usedGELimitLabel.setForeground(Color.ORANGE);
        } else {
            usedGELimitLabel.setForeground(Color.BLACK);
        }

        model.showQuotesFor(name, quotes, items);

        for (int i = 0; i < itemChooser.getItemCount(); i++) {
            if (itemChooser.getItemAt(i).getName().equals(name)) {
                itemChooser.setSelectedIndex(i);
                break;
            }
        }
    }

    public void showQuotesForSelectedItem(QuoteManager quotes, ItemManager items, TradeManager trades) throws SQLException {
        Item item = itemChooser.getItemAt(itemChooser.getSelectedIndex());
        String name;
        if (item == null) {
            return;
        } else {
            name = item.getName();
        }

        showQuotesFor(name, quotes, items, trades);
    }
}
