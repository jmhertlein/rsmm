package net.jmhertlein.rsmm.view.quote;

import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;

public class RecentQuotesTableModel extends AbstractTableModel {
    private final QuoteManager quotes;
    private final ArrayList<Quote> quoteCache;

    public RecentQuotesTableModel(QuoteManager quotes) {
        this.quotes = quotes;
        this.quoteCache = new ArrayList<>();
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Date";
            case 1:
                return "Bid";
            case 2:
                return "Ask";
            default:
                return "";
        }
    }

    @Override
    public int getRowCount() {
        return 5;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int row, int col) {
        row -= (5 - quoteCache.size());
        System.out.println(String.format("Row: %s, Col: %s", row, col));
        if (row >= quoteCache.size() || row < 0) {
            System.out.println("Cache miss.");
            return null;
        }

        Quote quote = quoteCache.get(row);
        switch (col) {
            case 0:
                return quote.getQuoteTS();
            case 1:
                return quote.getBid();
            case 2:
                return quote.getAsk();
            default:
                return null;
        }
    }

    public void showQuotesFor(String itemName) {
        quoteCache.clear();
        try {
            quoteCache.addAll(quotes.getLatestQuotes(itemName));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Quotes For " + itemName, JOptionPane.ERROR_MESSAGE);
        }
        fireTableDataChanged();
    }
}
