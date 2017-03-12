package cafe.josh.rsmm.model.update;

import cafe.josh.rsmm.model.Quote;

/**
 * Created by joshua on 4/9/16.
 */
public interface QuoteListener {
    public void onQuote(Quote q);

    public default void onMarkSynthetic(Quote q) {
        onQuote(q);
    }

    public default void quoteDeleted(Quote q) {
        onQuote(q);
    }
}
