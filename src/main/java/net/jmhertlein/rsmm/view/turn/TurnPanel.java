package net.jmhertlein.rsmm.view.turn;

import net.jmhertlein.rsmm.controller.turn.CloseTurnAction;
import net.jmhertlein.rsmm.controller.turn.TurnSelectedAction;
import net.jmhertlein.rsmm.controller.turn.NewTurnAction;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.model.update.TradeUpdateManager;
import net.jmhertlein.rsmm.view.trade.TradePanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Created by joshua on 1/16/16.
 */
public class TurnPanel extends JPanel {
    private final JTable turnsTable;
    private final TurnTableModel model;
    private final JButton openTurnButton, closeTurnButton;

    public TurnPanel(TurnManager turns, QuoteManager quotes, ItemManager items) {
        model = new TurnTableModel(quotes);
        turnsTable = new JTable(model);
        openTurnButton = new JButton(new NewTurnAction(this, items, turns, model));
        closeTurnButton = new JButton();

        turnsTable.setRowSelectionAllowed(true);
        turnsTable.setCellSelectionEnabled(true);
        turnsTable.setColumnSelectionAllowed(false);
        turnsTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        add(new JScrollPane(turnsTable), c);

        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.5;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(openTurnButton, c);
        c.gridx = 1;
        add(closeTurnButton, c);

        reload(turns);
    }

    public void addTableSelectionListener(TradePanel p) {
        turnsTable.getSelectionModel().addListSelectionListener(new TurnSelectedAction(turnsTable, model, p));
    }

    public JTable getTurnTable() {
        return turnsTable;
    }

    public TurnTableModel getTurnTableModel() {
        return model;
    }

    public void setCloseTurnAction(AbstractAction a) {
        closeTurnButton.setAction(a);
    }

    public void reload(TurnManager turns) {
        int selectedRow = turnsTable.getSelectedRow();
        model.reloadTurns(turns);
        if (selectedRow >= 0 && selectedRow < model.getRowCount()) {
            turnsTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    public Optional<Turn> getSelectedTurn() {
        return model.getTurnAtRow(turnsTable.getSelectedRow());
    }
}
