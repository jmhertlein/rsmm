package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.NoQuoteException;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.update.TradeListener;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;

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
