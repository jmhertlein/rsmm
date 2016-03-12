package net.jmhertlein.rsmm.viewfx;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.update.UpdateListener;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 3/11/16.
 */
public class RepopulateQuotesListener implements UpdateListener {
    private final ComboBox<Item> quoteItemChooser;
    private final ObservableList<Quote> shownQuotes;
    private final QuoteManager quotes;

    public RepopulateQuotesListener(ComboBox<Item> quoteItemChooser, ObservableList<Quote> shownQuotes, QuoteManager quotes) {
        this.quoteItemChooser = quoteItemChooser;
        this.shownQuotes = shownQuotes;
        this.quotes = quotes;
    }

    @Override
    public void onUpdate() throws SQLException {
        Optional.ofNullable(quoteItemChooser.getSelectionModel().getSelectedItem()).ifPresent(item -> {
            try {
                shownQuotes.setAll(quotes.getLatestQuotes(item.getName()));
            } catch (SQLException e) {
                Dialogs.showMessage("Quote Reload Error", "Error reloading quotes on update.", e);
            }
        });
    }
}
