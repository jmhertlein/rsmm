package net.jmhertlein.rsmm.controller;

import javafx.collections.ObservableList;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.update.QuoteListener;

/**
 * Created by joshua on 5/13/16.
 */
public class QuoteTableQuoteListener implements QuoteListener {
    private final ObservableList<Quote> visibleQuotes;

    public QuoteTableQuoteListener(ObservableList<Quote> visibleQuotes) {
        this.visibleQuotes = visibleQuotes;
    }

    @Override
    public void onQuote(Quote q) {
        visibleQuotes.add(q);
    }

    @Override
    public void onMarkSynthetic(Quote q) {
    }

    @Override
    public void quoteDeleted(Quote q) {
        visibleQuotes.removeAll(q);
    }
}
