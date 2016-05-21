package net.jmhertlein.rsmm.model.update;

import net.jmhertlein.rsmm.model.Trade;

/**
 * Created by joshua on 4/9/16.
 */
public interface TradeListener {
    public void onTrade(Trade t);
    public void onBust(Trade t);
}
