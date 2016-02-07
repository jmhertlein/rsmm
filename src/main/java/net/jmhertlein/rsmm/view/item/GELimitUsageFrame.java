package net.jmhertlein.rsmm.view.item;

import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.TradeManager;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by joshua on 2/6/16.
 */
public class GELimitUsageFrame extends JDialog {
    private final JTable limitUsageTable;

    public GELimitUsageFrame(TradeManager trades, ItemManager items) throws SQLException {
        setTitle("GE Limit Usage Summary");
        limitUsageTable = new JTable(new GELimitUsageTableModel(trades, items));

        setLayout(new BorderLayout());
        add(new JScrollPane(limitUsageTable), BorderLayout.CENTER);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setSize(400, 200);
        setModalityType(ModalityType.APPLICATION_MODAL);
    }
}
