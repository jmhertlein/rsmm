package cafe.josh.rsmm.controller;

import cafe.josh.joshfx.Dialogs;
import cafe.josh.rsmm.model.QuoteManager;

import cafe.josh.rsmm.model.NoQuoteException;
import cafe.josh.rsmm.model.Quote;
import cafe.josh.rsmm.model.TurnManager;
import cafe.josh.rsmm.model.update.QuoteListener;

import java.sql.SQLException;

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
