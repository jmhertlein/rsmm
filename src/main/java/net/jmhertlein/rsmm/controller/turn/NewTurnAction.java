package net.jmhertlein.rsmm.controller.turn;

import net.jmhertlein.rsmm.model.DuplicateOpenTurnException;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.view.turn.TurnTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 1/16/16.
 */
public class NewTurnAction extends AbstractAction {
    private final Component parent;
    private final ItemManager items;
    private final TurnManager turns;
    private final TurnTableModel turnTable;

    public NewTurnAction(Component parent, ItemManager items, TurnManager turns, TurnTableModel model) {
        super("New Turn");
        this.parent = parent;
        this.items = items;
        this.turns = turns;
        this.turnTable = model;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Optional<Item> chosen;
        try {
            chosen = Optional.ofNullable((Item) JOptionPane.showInputDialog(parent, "Please choose an item.", "Open New Turn", JOptionPane.PLAIN_MESSAGE, null, items.getItems().toArray(), null));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Error Getting Items", JOptionPane.ERROR_MESSAGE);
            return;
        }

        chosen.ifPresent(i -> {
            try {
                turns.newTurn(i.getName());
                turns.fireUpdateEvent();
            } catch (SQLException | DuplicateOpenTurnException e) {
                JOptionPane.showMessageDialog(parent, e.getMessage(), "Error Opening Turn", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
