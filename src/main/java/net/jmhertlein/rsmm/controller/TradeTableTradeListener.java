package net.jmhertlein.rsmm.controller;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.update.TradeListener;

/**
 * Created by joshua on 5/13/16.
 */
public class TradeTableTradeListener implements TradeListener {
    private final ObservableList<Trade> visibleTrades;
    private final ReadOnlyObjectProperty<Turn> selectedTurn;

    public TradeTableTradeListener(ObservableList<Trade> visibleTrades, ReadOnlyObjectProperty<Turn> selectedTurn) {
        this.visibleTrades = visibleTrades;
        this.selectedTurn = selectedTurn;
    }

    @Override
    public void onTrade(Trade t) {
        if(selectedTurn.isNotNull().get() && selectedTurn.get().equals(t.getTurn()))
        {
            visibleTrades.add(t);
        }
    }

    @Override
    public void onBust(Trade t) {
        if(selectedTurn.isNotNull().get() && selectedTurn.get().equals(t.getTurn()))
        {
            visibleTrades.remove(t);
        }
    }
}
