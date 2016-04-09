package net.jmhertlein.rsmm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by joshua on 3/25/16.
 */
public class GlobalStatsManager {
    private final IntegerProperty totalProfit, profitToday, pendingprofit, openProfit, sumPositionCost;

    public GlobalStatsManager() {
        totalProfit = new SimpleIntegerProperty();
        profitToday = new SimpleIntegerProperty();
        pendingprofit = new SimpleIntegerProperty();
        openProfit = new SimpleIntegerProperty();
        sumPositionCost = new SimpleIntegerProperty();
    }
}
