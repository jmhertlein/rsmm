package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.view.quote.RecentQuotesTableModel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class QuoteItemSelectedAction implements ItemListener {
    private final QuoteManager quotes;
    private final ItemManager items;
    private final JComboBox<Item> chooser;
    private final RecentQuotesTableModel tableModel;

    public QuoteItemSelectedAction(QuoteManager quotes, ItemManager items, JComboBox<Item> chooser, RecentQuotesTableModel tableModel) {
        this.quotes = quotes;
        this.items = items;
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

        tableModel.showQuotesFor(name, quotes, items);
    }
}
