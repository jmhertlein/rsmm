package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.model.TradeManager;
import net.jmhertlein.rsmm.view.quote.QuotePanel;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/17/16.
 */
public class AddTradeAction extends AbstractAction {
    private final TradePanel tradePanel;
    private final QuotePanel quotePanel;
    private final TurnPanel turnPanel;
    private final TurnManager turns;
    private final TradeManager trades;
    private final boolean buy;

    public AddTradeAction(TradePanel tradePanel, QuotePanel quotePanel, TurnPanel turnPanel, TurnManager turns, TradeManager trades, boolean buy) {
        super(buy ? "Buy" : "Sell");
        this.turns = turns;
        this.trades = trades;
        this.buy = buy;
        this.turnPanel = turnPanel;
        this.quotePanel = quotePanel;
        this.tradePanel = tradePanel;

    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        Optional<Quote> q = quotePanel.getSelectedQuote();
        if (!q.isPresent()) {
            JOptionPane.showMessageDialog(quotePanel, "Select a quote to use.", "No Quote Selected", JOptionPane.ERROR_MESSAGE);
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
            qty = Integer.parseInt(tradePanel.getQuantityField().getText());
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(tradePanel, "Invalid quantity.", "Error Parsing Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(tradePanel, "Quantity cannot be negative or zero.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<Turn> t = turnPanel.getSelectedTurn();
        if (!t.isPresent()) {
            JOptionPane.showMessageDialog(turnPanel, "No turn selected.", "Invalid Turn", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!q.get().getItemName().equals(t.get().getItemName())) {
            JOptionPane.showMessageDialog(quotePanel, "The selected quote is not for the item being traded.", "Incorrect Item", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!buy) {
            qty *= -1;
        }

        try {
            t.get().addTrade(px, qty);
            trades.fireUpdateEvent();
            tradePanel.getQuantityField().setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Adding Trade", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
