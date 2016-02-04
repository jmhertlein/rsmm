package net.jmhertlein.rsmm.view.turn;

import net.jmhertlein.rsmm.model.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by joshua on 1/16/16.
 */
public class TurnTableModel extends AbstractTableModel {
    private final QuoteManager quotes;
    private final ItemManager items;
    private final List<Turn> turnsCache;

    public TurnTableModel(QuoteManager quotes, ItemManager items) {
        this.quotes = quotes;
        this.items = items;
        turnsCache = new ArrayList<>();
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Item";
            case 1:
                return "Position";
            case 2:
                return "Bought Volume";
            case 3:
                return "GE Limit";
            case 4:
                return "Position Cost";
            case 5:
                return "Open Profit";
            case 6:
                return "Closed Profit";
            default:
                return "";
        }
    }

    @Override
    public int getRowCount() {
        return turnsCache.size();
    }

    @Override
    public int getColumnCount() {
        return 7;
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (row > turnsCache.size()) {
            return null;
        }
        Turn turn = turnsCache.get(row);
        try {
            switch (col) {
                case 0:
                    return turn.getItemName();
                case 1:
                    return turn.getPosition();
                case 2:
                    return turn.getBoughtVolume();
                case 3:
                    return items.getItem(turn.getItemName()).orElse(new Item("UNK", -1)).getBuyLimit();
                case 4:
                    return turn.getPositionCost(quotes);
                case 5:
                    return new RSInteger(turn.getOpenProfit(quotes));
                case 6:
                    return new RSInteger(turn.getClosedProfit());
                default:
                    return null;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Turn Field", JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (NoQuoteException e) {
            return null;
        } catch (ArithmeticException ex) {
            //TODO: this is awful please fix it
            return "NaN";
        }
    }

    public void reloadTurns(TurnManager turns) {
        turnsCache.clear();
        try {
            turnsCache.addAll(turns.getOpenTurns());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error Getting Turns", JOptionPane.ERROR_MESSAGE);
        }
        fireTableDataChanged();
    }

    public Optional<Turn> getTurnAtRow(int selectedRow) {
        if (selectedRow < 0 || selectedRow >= turnsCache.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(turnsCache.get(selectedRow));
    }
}
