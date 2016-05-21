package net.jmhertlein.rsmm.controller.util;

/**
 * Created by joshua on 5/13/16.
 */
public enum Side {
    BID(1), ASK(-1);

    private final int multiplier;

    Side(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
