package net.jmhertlein.rsmm.controller.item;

import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.view.item.GELimitUsageFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

/**
 * Created by joshua on 2/6/16.
 */
public class ShowGELimitUsageAction extends AbstractAction {
    private final JFrame parent;
    private final ItemManager items;
    private final TradeManager trades;

    public ShowGELimitUsageAction(JFrame parent, ItemManager items, TradeManager trades) {
        super("GE Limit Usage...");
        this.parent = parent;
        this.items = items;
        this.trades = trades;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        try {
            GELimitUsageFrame f = new GELimitUsageFrame(trades, items);
            f.setLocationRelativeTo(parent);
            f.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parent, e.getMessage(), "Error Getting GE Limit Usages", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }
    }
}
