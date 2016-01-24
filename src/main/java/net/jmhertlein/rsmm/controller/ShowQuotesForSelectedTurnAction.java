package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.view.quote.QuotePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by joshua on 1/24/16.
 */
public class ShowQuotesForSelectedTurnAction extends AbstractAction {
    private final TurnPanel turnPanel;
    private final QuotePanel quotePanel;

    public ShowQuotesForSelectedTurnAction(TurnPanel turnPanel, QuotePanel quotePanel) {
        super(">");
        this.turnPanel = turnPanel;
        this.quotePanel = quotePanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        turnPanel.getSelectedTurn().ifPresent(t -> quotePanel.showQuotesFor(t.getItemName()));
    }
}
