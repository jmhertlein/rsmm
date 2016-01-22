package net.jmhertlein.rsmm.controller.turn;

import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/18/16.
 */
public class CloseTurnAction extends AbstractAction {
    private final TurnPanel turnPanel;
    private final TradePanel tradePanel;
    private final TurnManager turns;

    public CloseTurnAction(TurnManager turns, TurnPanel turnPanel, TradePanel tradePanel) {
        super("Close Turn");
        this.turns = turns;
        this.turnPanel = turnPanel;
        this.tradePanel = tradePanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Optional<Turn> turnAtRow = turnPanel.getSelectedTurn();
        if (turnAtRow.isPresent()) {
            try {
                turnAtRow.get().closeTurn();
                tradePanel.clearCache();
                turns.fireUpdateEvent();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(turnPanel, e.getMessage(), "Error Closing Turn", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(turnPanel, "No turn selected.", "Error Closing Turn", JOptionPane.ERROR_MESSAGE);
        }
    }
}
