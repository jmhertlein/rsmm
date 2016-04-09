package net.jmhertlein.rsmm.view.profit;

import net.jmhertlein.rsmm.model.NoQuoteException;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.RSInteger;
import net.jmhertlein.rsmm.model.TurnManager;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Created by joshua on 1/29/16.
 */
public class ProfitPanel extends JPanel {
    private final GPLabel totalProfitLabel, closedProfitLabel, openProfitLabel, pendingProfitLabel, positionCostLabel;

    public ProfitPanel(TradeManager trades, TurnManager turns, QuoteManager quotes) {
        totalProfitLabel = new GPLabel();
        closedProfitLabel = new GPLabel();
        openProfitLabel = new GPLabel();
        pendingProfitLabel = new GPLabel();
        positionCostLabel = new GPLabel();

        trades.addListener(() -> updateProfit(turns, quotes));
        turns.addListener(() -> updateProfit(turns, quotes));
        quotes.addListener(() -> updateProfit(turns, quotes));

        updateProfit(turns, quotes);

        setupUI();
    }

    private void setupUI() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalGlue());

        add(new JLabel("Total: "));
        add(totalProfitLabel);
        add(Box.createHorizontalGlue());

        add(new JLabel("Today: "));
        add(closedProfitLabel);
        add(Box.createHorizontalGlue());

        add(new JLabel("Pending: "));
        add(pendingProfitLabel);
        add(Box.createHorizontalGlue());

        add(new JLabel("Open: "));
        add(openProfitLabel);
        add(Box.createHorizontalGlue());

        add(new JLabel("Sum Position Cost: "));
        add(positionCostLabel);

        add(Box.createHorizontalGlue());
    }

    private void updateProfit(TurnManager turns, QuoteManager quotes) {
        try {
            totalProfitLabel.setPx(turns.getTotalClosedProfit().orElse(new RSInteger(0)));
            closedProfitLabel.setPx(turns.getClosedProfitForDay(LocalDate.now()).orElse(new RSInteger(0)));
            openProfitLabel.setPx(turns.getTotalOpenProfit(quotes));
            pendingProfitLabel.setPx(turns.getOpenTurnClosedProfit());
            positionCostLabel.setPx(turns.getTotalPositionCost(quotes));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error Updating Profit", JOptionPane.ERROR_MESSAGE);
        } catch (NoQuoteException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error Updating Profit", JOptionPane.ERROR_MESSAGE);
        }
    }
}
