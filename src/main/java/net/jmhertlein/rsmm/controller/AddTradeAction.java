package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.view.quote.RecentQuotesTableModel;
import net.jmhertlein.rsmm.view.trade.TradeTableModel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/17/16.
 */
public class AddTradeAction extends AbstractAction {
    private final JTextField quantityField;
    private final JTable tradeTable;
    private final TradeTableModel tradeTableModel;
    private final JComboBox<Item> quoteItemChooser;
    private final JTable turnTable;
    private final TurnTableModel turnTableModel;
    private final JTable quoteTable;
    private final RecentQuotesTableModel quoteTableModel;
    private final boolean buy;

    public AddTradeAction(JTextField quantityField, JTable tradeTable, TradeTableModel tradeTableModel, JComboBox<Item> quoteItemChooser, JTable turnTable, TurnTableModel turnTableModel, JTable quoteTable, RecentQuotesTableModel quoteTableModel, boolean buy) {
        super(buy ? "Buy" : "Sell");
        this.quantityField = quantityField;
        this.tradeTable = tradeTable;
        this.tradeTableModel = tradeTableModel;
        this.quoteItemChooser = quoteItemChooser;
        this.turnTable = turnTable;
        this.turnTableModel = turnTableModel;
        this.quoteTable = quoteTable;
        this.quoteTableModel = quoteTableModel;
        this.buy = buy;
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int quoteRow = quoteTable.getSelectedRow();
        Optional<Quote> q = quoteTableModel.getQuoteAt(quoteRow);
        if (!q.isPresent()) {
            JOptionPane.showMessageDialog(quoteTable, "Select a quote to use.", "No Quote Selected", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int px;
        if (buy) {
            px = q.get().getBid().intValue();
        } else {
            px = q.get().getAsk().intValue();
        }

        int qty;
        try {
            qty = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(quantityField, "Invalid quantity.", "Error Parsing Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(quantityField, "Quantity cannot be negative or zero.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<Turn> t = turnTableModel.getTurnAtRow(turnTable.getSelectedRow());
        if (!t.isPresent()) {
            JOptionPane.showMessageDialog(quoteTable, "No turn selected.", "Invalid Turn", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!quoteItemChooser.getItemAt(quoteItemChooser.getSelectedIndex()).getName().equals(t.get().getItemName())) {
            JOptionPane.showMessageDialog(quoteTable, "The selected quote is not for the item being traded.", "Incorrect Item", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!buy) {
            qty *= -1;
        }

        try {
            t.get().addTrade(px, qty);
            int r = turnTable.getSelectedRow();
            turnTableModel.fireTableDataChanged();
            turnTable.setRowSelectionInterval(r, r);
            tradeTableModel.showTradesFor(t.get());
            quantityField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Adding Trade", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
