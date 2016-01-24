package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.view.quote.RecentQuotesTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.List;

public class QuoteItemSelectedAction implements ItemListener {
    private final QuoteManager quotes;
    private final JComboBox<Item> chooser;
    private final RecentQuotesTableModel tableModel;

    public QuoteItemSelectedAction(QuoteManager quotes, JComboBox<Item> chooser, RecentQuotesTableModel tableModel) {
        this.quotes = quotes;
        this.chooser = chooser;
        this.tableModel = tableModel;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        Item item = chooser.getItemAt(chooser.getSelectedIndex());
        String name;
        if (item == null) {
            return;
        } else {
            name = item.getName();
        }

        tableModel.showQuotesFor(name, quotes);
    }
}
