package net.jmhertlein.rsmm.view.trade;

import net.jmhertlein.rsmm.model.RSInteger;
import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.Turn;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by joshua on 1/17/16.
 */
public class TradeTableModel extends AbstractTableModel {
    private static final SimpleDateFormat FMT = new SimpleDateFormat("MM/dd HH:mm");
    private final List<Trade> tradeCache;

    public TradeTableModel() {
        tradeCache = new ArrayList<>();
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Time";
            case 1:
                return "Quantity";
            case 2:
                return "Price";
            default:
                return "";
        }
    }

    @Override
    public int getRowCount() {
        return tradeCache.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row >= tradeCache.size()) {
            return null;
        }

        Trade t = tradeCache.get(row);
        switch (col) {
            case 0:
                return FMT.format(Timestamp.valueOf(t.getTradeTime()));
            case 1:
                return t.getQuantity();
            case 2:
                return new RSInteger(t.getPrice());
            default:
                return null;
        }
    }

    public void showTradesFor(Turn t) {
        tradeCache.clear();
        try {
            tradeCache.addAll(t.getTrades());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Trades For " + t.getItemName(), JOptionPane.ERROR_MESSAGE);
            return;
        }

        fireTableDataChanged();
    }

    public void clearCache() {
        tradeCache.clear();
        fireTableDataChanged();
    }

    public Optional<Trade> getTradeAtRow(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= tradeCache.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(tradeCache.get(selectedRow));
    }
}
