package net.jmhertlein.rsmm.view.turn;

import net.jmhertlein.rsmm.controller.turn.NewTurnAction;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.TurnManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by joshua on 1/16/16.
 */
public class TurnPanel extends JPanel {
    private final JTable turnsTable;
    private final TurnTableModel model;
    private final JButton openTurnButton, closeTurnButton;

    public TurnPanel(TurnManager turns, QuoteManager quotes, ItemManager items) {
        model = new TurnTableModel(turns, quotes);
        turnsTable = new JTable(model);
        openTurnButton = new JButton(new NewTurnAction(this, items, turns, model));
        closeTurnButton = new JButton("Close Turn");
        closeTurnButton.setEnabled(false);

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
    }


}