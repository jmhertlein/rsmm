package net.jmhertlein.rsmm.view.quote;

import net.jmhertlein.rsmm.model.Quote;
import net.jmhertlein.rsmm.model.QuoteManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class RecentQuotesTableModel extends AbstractTableModel {
    private static final SimpleDateFormat FMT = new SimpleDateFormat("MM/dd HH:mm");
    private final QuoteManager quotes;
    private final Quote[] quoteCache;

    public RecentQuotesTableModel(QuoteManager quotes) {
        this.quotes = quotes;
        this.quoteCache = new Quote[5];
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
            case 3:
                return "Spread";
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
        return 4;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row >= quoteCache.length || row < 0) {
            return null;
        }

        Quote quote = quoteCache[row];
        if (quote == null) {
            return null;
        }

        switch (col) {
            case 0:
                return FMT.format(quote.getQuoteTS());
            case 1:
                return quote.getBid();
            case 2:
                return quote.getAsk();
            case 3:
                return quote.getAsk().intValue() - quote.getBid().intValue();
            default:
                return null;
        }
    }

    public void showQuotesFor(String itemName) {
        for (int i = 0; i < quoteCache.length; i++) {
            quoteCache[i] = null;
        }

        try {
            Object[] srcList = quotes.getLatestQuotes(itemName).toArray();
            for (int srcPos = 0, dstPos = quoteCache.length - 1; dstPos >= 0 && srcPos < srcList.length; srcPos++, dstPos--) {
                quoteCache[dstPos] = (Quote) srcList[srcPos];
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Quotes For " + itemName, JOptionPane.ERROR_MESSAGE);
        }
        fireTableDataChanged();
    }

    public Optional<Quote> getQuoteAt(int quoteRow) {
        System.out.println("For row " + quoteRow);
        if (quoteRow < 0 || quoteRow >= quoteCache.length) {
            return Optional.empty();
        }
        return Optional.ofNullable(quoteCache[quoteRow]);
    }
}
