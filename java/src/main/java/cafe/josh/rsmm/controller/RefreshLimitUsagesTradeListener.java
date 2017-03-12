package cafe.josh.rsmm.controller;

import cafe.josh.rsmm.model.update.TradeListener;
import cafe.josh.rsmm.model.Trade;

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
