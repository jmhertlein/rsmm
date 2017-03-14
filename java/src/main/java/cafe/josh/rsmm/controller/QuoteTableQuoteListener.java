package cafe.josh.rsmm.controller;

import cafe.josh.rsmm.model.Quote;
import cafe.josh.rsmm.model.update.QuoteListener;
import javafx.collections.ObservableList;

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
