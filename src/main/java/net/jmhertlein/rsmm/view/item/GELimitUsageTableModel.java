package net.jmhertlein.rsmm.view.item;

import net.jmhertlein.rsmm.model.ItemManager;

import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by joshua on 2/6/16.
 */
public class GELimitUsageTableModel extends DefaultTableModel {
    private final String[] names;
    private final int[] usages;
    private final int[] limits;

    public GELimitUsageTableModel(TradeManager trades, ItemManager items) throws SQLException {
        Map<String, Integer> m = trades.getOutstandingGELimits();

        names = new String[m.size()];
        usages = new int[m.size()];
        limits = new int[m.size()];

        int row = 0;
        for (Map.Entry<String, Integer> e : m.entrySet()) {
            names[row] = e.getKey();
            usages[row] = e.getValue();
            limits[row] = items.getLimitFor(e.getKey()).get();
            row++;
        }
    }

    @Override
    public int getRowCount() {
        return names == null ? 0 : names.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int i) {
        switch (i) {
            case 0:
                return "Item";
            case 1:
                return "Used";
            case 2:
                return "Limit";
            default:
                return "ERR";
        }
    }

    @Override
    public Object getValueAt(int r, int c) {
        if (r > names.length || r < 0 || c < 0 || c > 2) {
            return null;
        }

        switch (c) {
            case 0:
                return names[r];
            case 1:
                return usages[r];
            case 2:
                return limits[r];
            default:
                return "ERR";
        }
    }
}
