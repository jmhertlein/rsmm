package cafe.josh.rsmm.controller;

import cafe.josh.rsmm.model.NoQuoteException;
import cafe.josh.rsmm.model.QuoteManager;
import cafe.josh.rsmm.model.Trade;
import cafe.josh.rsmm.model.update.TradeListener;
import cafe.josh.rsmm.viewfx.util.Dialogs;

import java.sql.SQLException;

/**
 * Created by joshua on 5/13/16.
 */
public class RecalculateProfitOnTradeListener implements TradeListener {
    private final QuoteManager quotes;

    public RecalculateProfitOnTradeListener(QuoteManager quotes) {
        this.quotes = quotes;
    }

    @Override
    public void onTrade(Trade t) {
        try {
            t.getTurn().recalculateProfit(quotes);
        } catch (NoQuoteException | SQLException e) {
            Dialogs.showMessage("Error Recalculating Turn Profit", "Error Recalculating Turn Profit", e);
        }
    }

    @Override
    public void onBust(Trade t) {
        try {
            t.getTurn().recalculateProfit(quotes);
        } catch (NoQuoteException | SQLException e) {
            Dialogs.showMessage("Error Recalculating Turn Profit", "Error Recalculating Turn Profit", e);
        }
    }
}
