package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.view.quote.QuotePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Created by joshua on 1/24/16.
 */
public class ShowQuotesForSelectedTurnAction extends AbstractAction {
    private final TurnPanel turnPanel;
    private final QuotePanel quotePanel;
    private final QuoteManager quotes;
    private final ItemManager items;
    private final TradeManager trades;

    public ShowQuotesForSelectedTurnAction(TurnPanel turnPanel, QuotePanel quotePanel, QuoteManager quotes, ItemManager items, TradeManager trades) {
        super(">");
        this.turnPanel = turnPanel;
        this.quotePanel = quotePanel;
        this.quotes = quotes;
        this.items = items;
        this.trades = trades;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        turnPanel.getSelectedTurn().ifPresent(t -> {
            try {
                quotePanel.showQuotesFor(t.getItemName(), quotes, items, trades);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(quotePanel, e.getMessage(), "Error Showing Quotes", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
