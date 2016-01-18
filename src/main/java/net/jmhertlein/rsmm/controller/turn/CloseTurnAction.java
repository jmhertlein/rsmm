package net.jmhertlein.rsmm.controller.turn;

import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/18/16.
 */
public class CloseTurnAction extends AbstractAction {
    private final JTable turnTable;
    private final TurnTableModel model;
    private final TradePanel tradePanel;

    public CloseTurnAction(JTable turnTable, TurnTableModel model, TradePanel tradePanel) {
        super("Close Turn");
        this.turnTable = turnTable;
        this.model = model;
        this.tradePanel = tradePanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int selectedRow = turnTable.getSelectedRow();
        Optional<Turn> turnAtRow = model.getTurnAtRow(selectedRow);
        if (turnAtRow.isPresent()) {
            try {
                turnAtRow.get().closeTurn();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(turnTable, e.getMessage(), "Error Closing Turn", JOptionPane.ERROR_MESSAGE);
            }
            model.reloadTurns();
            tradePanel.clearCache();
        } else {
            JOptionPane.showMessageDialog(turnTable, "No turn selected.", "Error Closing Turn", JOptionPane.ERROR_MESSAGE);
        }
    }
}
