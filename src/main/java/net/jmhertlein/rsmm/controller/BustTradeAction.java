package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.view.trade.TradeTableModel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/18/16.
 */
public class BustTradeAction extends AbstractAction {
    private final JTable tradeTable;
    private final TradeTableModel tradeTableModel;
    private final TurnTableModel turnTableModel;

    public BustTradeAction(JTable tradeTable, TradeTableModel tradeTableModel, TurnTableModel turnTableModel) {
        super("Bust Trade");
        this.tradeTable = tradeTable;
        this.tradeTableModel = tradeTableModel;
        this.turnTableModel = turnTableModel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (JOptionPane.showConfirmDialog(tradeTable, "Really bust trade?") != 0) {
            return;
        }

        Optional<Trade> t = tradeTableModel.getTradeAtRow(tradeTable.getSelectedRow());

        if (t.isPresent()) {
            try {
                t.get().bustTrade();
                tradeTableModel.showTradesFor(t.get().getTurn());
                turnTableModel.fireTableDataChanged();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(tradeTable, e.getMessage(), "Error Busting Trade", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(tradeTable, "No trade selected.", "Error Busting Trade", JOptionPane.ERROR_MESSAGE);
        }
    }
}
