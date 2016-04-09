package net.jmhertlein.rsmm.model.update;

import net.jmhertlein.rsmm.model.Quote;

/**
 * Created by joshua on 4/9/16.
 */
@FunctionalInterface
public interface QuoteListener {
    public void onQuote(Quote q);
}
