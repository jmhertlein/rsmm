package cafe.josh.rsmm.model;

import cafe.josh.rsmm.model.update.TurnListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import cafe.josh.rsmm.model.update.QuoteListener;
import cafe.josh.rsmm.model.update.TradeListener;
import cafe.josh.rsmm.viewfx.util.Dialogs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by joshua on 3/25/16.
 */
public class GlobalStatsManager implements TradeListener, TurnListener, QuoteListener {
    private final LongProperty totalProfit;
    private final IntegerProperty profitToday;
    private final IntegerProperty pendingprofit;
    private final IntegerProperty openProfit;
    private final IntegerProperty sumPositionCost;
    private final IntegerProperty dailyQuoteCost;

    private final Connection conn;
    private final TurnManager turns;
    private final QuoteManager quotes;

    public GlobalStatsManager(Connection conn, TurnManager turns, QuoteManager quotes) {
        this.conn = conn;
        this.turns = turns;
        this.quotes = quotes;
        totalProfit = new SimpleLongProperty();
        profitToday = new SimpleIntegerProperty();
        pendingprofit = new SimpleIntegerProperty();
        openProfit = new SimpleIntegerProperty();
        sumPositionCost = new SimpleIntegerProperty();
        dailyQuoteCost = new SimpleIntegerProperty();
    }

    public LongProperty totalClosedProfitProperty() {
        return totalProfit;
    }

    public IntegerProperty profitTodayProperty() {
        return profitToday;
    }

    public IntegerProperty pendingProfitProperty() {
        return pendingprofit;
    }

    public IntegerProperty openProfitProperty() {
        return openProfit;
    }

    public IntegerProperty sumPositionCostProperty() {
        return sumPositionCost;
    }

    public IntegerProperty dailyQuoteCostProperty() {
        return dailyQuoteCost;
    }

    public void recalculateTotalProfit() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(price * (quantity*-1)) AS total_closed FROM Trade NATURAL JOIN Turn WHERE close_ts IS NOT NULL;")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                totalProfit.set(rs.getLong("total_closed"));
            }
        }
    }

    public void recalculateTodayProfit() {
        profitToday.set(turns.getClosedProfitForDay());
    }

    public void recalculatePendingProfit() {
        pendingprofit.set(turns.getOpenTurnClosedProfit());
    }

    public void recalculateOpenProfit() throws NoQuoteException, SQLException, NoSuchItemException {
        openProfit.set(turns.getTotalOpenProfit(quotes));
    }

    public void recalculateSumPositionCost() throws NoQuoteException, SQLException, NoSuchItemException {
        sumPositionCost.set(turns.getTotalPositionCost(quotes));
    }

    public void recalculateDailyQuoteCost() throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT SUM(ask1 - bid1) AS daily_quote_cost FROM Quote WHERE quote_ts::date = now()::date AND NOT synthetic;")) {
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                dailyQuoteCost.set(rs.getInt("daily_quote_cost"));
            }
        }
    }

    public void recalculateProfit() throws NoQuoteException, SQLException, NoSuchItemException {
        recalculateTotalProfit();
        recalculateOpenProfit();
        recalculatePendingProfit();
        recalculateSumPositionCost();
        recalculateTodayProfit();
    }

    @Override
    public void onQuote(Quote q) {
        try {
            recalculateProfit();
            recalculateDailyQuoteCost();
        } catch (NoQuoteException | SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Calculating Global Stats", "Error Calculating Global Stats", e);
        }
    }

    @Override
    public void onTrade(Trade t) {
        try {
            recalculateProfit();
        } catch (NoQuoteException | SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Calculating Global Stats", "Error Calculating Global Stats", e);
        }
    }

    @Override
    public void onBust(Trade t) {
        try {
            recalculateProfit();
        } catch (NoQuoteException | SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Calculating Global Stats", "Error Calculating Global Stats", e);
        }
    }

    @Override
    public void onTurnOpen(Turn t) {
        try {
            recalculateProfit();
        } catch (NoQuoteException | SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Calculating Global Stats", "Error Calculating Global Stats", e);
        }
    }

    @Override
    public void onTurnClose(Turn t) {
        try {
            recalculateProfit();
        } catch (NoQuoteException | SQLException | NoSuchItemException e) {
            Dialogs.showMessage("Error Calculating Global Stats", "Error Calculating Global Stats", e);
        }
    }
}
