package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.NoQuoteException;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.model.update.QuoteListener;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;

import java.sql.SQLException;

/**
 * Created by joshua on 5/13/16.
 */
public class RecalculateTurnStatsOnQuoteListener implements QuoteListener {
    private final TurnManager turns;
    private final QuoteManager quotes;

    public RecalculateTurnStatsOnQuoteListener(TurnManager turns, QuoteManager quotes) {
        this.turns = turns;
        this.quotes = quotes;
    }

    @Override
    public void onQuote(Quote q) {
        turns.getOpenTurn(q.getItem()).ifPresent(turn -> {
            try {
                turn.recalculateProfit(quotes);
            } catch (NoQuoteException | SQLException e) {
                Dialogs.showMessage("Error Handling Quote", "Error Updating Profit on Quote", e);
            }
        });
    }

    @Override
    public void onMarkSynthetic(Quote q) {

    }
}
