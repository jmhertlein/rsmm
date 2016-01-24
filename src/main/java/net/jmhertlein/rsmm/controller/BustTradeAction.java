package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.update.TradeUpdateManager;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.trade.TradeTableModel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/18/16.
 */
public class BustTradeAction extends AbstractAction {
    private final TradeUpdateManager trades;
    private final TradePanel tradePanel;

    public BustTradeAction(TradeUpdateManager trades, TradePanel tradePanel) {
        super("Bust Trade");
        this.tradePanel = tradePanel;
        this.trades = trades;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (JOptionPane.showConfirmDialog(tradePanel, "Really bust trade?") != 0) {
            return;
        }

        Optional<Trade> t = tradePanel.getSelectedTrade();

        if (t.isPresent()) {
            try {
                t.get().bustTrade();
                trades.fireUpdateEvent();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(tradePanel, e.getMessage(), "Error Busting Trade", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(tradePanel, "No trade selected.", "Error Busting Trade", JOptionPane.ERROR_MESSAGE);
        }
    }
}
