package net.jmhertlein.rsmm.controller.turn;

import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.Optional;

/**
 * Created by joshua on 1/17/16.
 */
public class TurnSelectedAction implements ListSelectionListener {
    private final JTable turnTable;
    private final TurnTableModel turnTableModel;
    private final TradePanel tradePanel;

    public TurnSelectedAction(JTable turnTable, TurnTableModel turnTableModel, TradePanel tradePanel) {
        this.turnTable = turnTable;
        this.turnTableModel = turnTableModel;
        this.tradePanel = tradePanel;
    }

    @Override
    public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting()) {
            return;
        }

        Optional<Turn> t = turnTableModel.getTurnAtRow(turnTable.getSelectedRow());
        t.ifPresent((turn) -> tradePanel.showTradesFor(turn));
    }
}