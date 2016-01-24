package net.jmhertlein.rsmm.view.trade;

import net.jmhertlein.rsmm.controller.BustTradeAction;
import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.update.TradeUpdateManager;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Created by joshua on 1/17/16.
 */
public class TradePanel extends JPanel {
    private final JTable tradeTable;
    private final TradeTableModel tradeTableModel;
    private final JTextField qtyField;
    private final JButton buyButton, sellButton;

    public TradePanel() {
        tradeTableModel = new TradeTableModel();
        tradeTable = new JTable(tradeTableModel);
        buyButton = new JButton();
        sellButton = new JButton();
        qtyField = new JTextField(13);

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        add(new JScrollPane(tradeTable), c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weighty = 0;
        c.weightx = 1;
        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        add(qtyField, c);

        c.weightx = 0;
        c.gridx = 1;
        c.fill = GridBagConstraints.NONE;
        add(buyButton, c);
        c.gridx = 2;
        add(sellButton, c);

        setPreferredSize(new Dimension(400, 600));
    }

    public void showTradesFor(Turn t) {
        tradeTableModel.showTradesFor(t);
    }

    public void setBuyAction(AbstractAction action) {
        buyButton.setAction(action);
    }

    public void setSellAction(AbstractAction action) {
        sellButton.setAction(action);
    }

    public JTextField getQuantityField() {
        return qtyField;
    }

    public JTable getTradeTable() {
        return tradeTable;
    }

    public TradeTableModel getTradeTableModel() {
        return tradeTableModel;
    }

    public void clearCache() {
        tradeTableModel.clearCache();
    }

    public Optional<Trade> getSelectedTrade() {
        return tradeTableModel.getTradeAtRow(tradeTable.getSelectedRow());
    }
}
