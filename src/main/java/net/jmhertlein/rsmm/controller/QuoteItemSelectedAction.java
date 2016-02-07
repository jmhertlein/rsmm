package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.TradeManager;
import net.jmhertlein.rsmm.view.quote.QuotePanel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

public class QuoteItemSelectedAction implements ItemListener {
    private final QuoteManager quotes;
    private final ItemManager items;
    private final TradeManager trades;
    private final QuotePanel panel;

    public QuoteItemSelectedAction(QuoteManager quotes, ItemManager items, TradeManager trades, QuotePanel panel) {
        this.quotes = quotes;
        this.items = items;
        this.trades = trades;
        this.panel = panel;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() != ItemEvent.SELECTED) {
            return;
        }

        try {
            panel.showQuotesForSelectedItem(quotes, items, trades);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error Looking Up Quotes", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
