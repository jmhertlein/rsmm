package net.jmhertlein.rsmm.controller;

import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.update.TradeListener;

/**
 * Created by joshua on 5/13/16.
 */
public class RefreshLimitUsagesTradeListener implements TradeListener {
    private final Runnable r;

    public RefreshLimitUsagesTradeListener(Runnable r) {
        this.r = r;
    }

    @Override
    public void onTrade(Trade t) {
        r.run();
    }

    @Override
    public void onBust(Trade t) {
        r.run();
    }
}
