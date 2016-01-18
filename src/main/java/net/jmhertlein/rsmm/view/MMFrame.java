/*
 * Copyright (C) 2016 joshua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jmhertlein.rsmm.view;

import java.awt.*;
import java.sql.Connection;
import javax.swing.*;

import net.jmhertlein.rsmm.controller.AddTradeAction;
import net.jmhertlein.rsmm.controller.BustTradeAction;
import net.jmhertlein.rsmm.controller.ShowItemManagerAction;
import net.jmhertlein.rsmm.controller.turn.CloseTurnAction;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.view.quote.QuotePanel;
import net.jmhertlein.rsmm.view.trade.TradePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

/**
 * @author joshua
 */
public class MMFrame extends JFrame {
    private final ShowItemManagerAction showItemManagerAction;
    private final QuotePanel quotePanel;
    private final TurnPanel turnPanel;
    private final TradePanel tradePanel;

    public MMFrame(Connection conn) {
        super("RS Market Maker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ItemManager items = new ItemManager(conn);
        QuoteManager quotes = new QuoteManager(conn);
        TurnManager turns = new TurnManager(conn);

        this.quotePanel = new QuotePanel(items, quotes);
        this.showItemManagerAction = new ShowItemManagerAction(items, this, quotePanel);

        this.turnPanel = new TurnPanel(turns, quotes, items);
        this.tradePanel = new TradePanel();
        turnPanel.addTableSelectionListener(tradePanel);

        tradePanel.setBuyAction(new AddTradeAction(tradePanel.getQuantityField(), tradePanel.getTradeTable(), tradePanel.getTradeTableModel(),
                quotePanel.getItemChooser(), turnPanel.getTurnTable(), turnPanel.getTurnTableModel(),
                quotePanel.getQuoteTable(), quotePanel.getQuoteTableModel(), true));
        tradePanel.setSellAction(new AddTradeAction(tradePanel.getQuantityField(), tradePanel.getTradeTable(), tradePanel.getTradeTableModel(),
                quotePanel.getItemChooser(), turnPanel.getTurnTable(), turnPanel.getTurnTableModel(),
                quotePanel.getQuoteTable(), quotePanel.getQuoteTableModel(), false));

        turnPanel.setCloseTurnAction(new CloseTurnAction(turnPanel.getTurnTable(), turnPanel.getTurnTableModel(), tradePanel));
        quotePanel.setAddQuoteAction(quotes, turnPanel);

        setSize(800, 600);
        setupMenus();
        setupUI();
        setIconImage(new ImageIcon(getClass().getResource("/coins.png")).getImage());
        hackWMClassName();
    }

    private final void hackWMClassName() {
        try {
            Toolkit xToolkit = Toolkit.getDefaultToolkit();
            java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField("awtAppClassName");
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, "RS Market Maker");
        } catch (Throwable t) {

        }
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JSplitPane lrPanel = new JSplitPane(), rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        rightPanel.setTopComponent(new FatPanel(quotePanel));
        rightPanel.setBottomComponent(turnPanel);

        lrPanel.setRightComponent(rightPanel);
        lrPanel.setLeftComponent(tradePanel);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(lrPanel, c);
    }

    private void setupMenus() {
        JMenuBar bar = new JMenuBar();

        JMenu itemsMenu = new JMenu("Items");
        itemsMenu.add(showItemManagerAction);
        bar.add(itemsMenu);

        JMenu tradesMenu = new JMenu("Trades");
        tradesMenu.add(new BustTradeAction(tradePanel.getTradeTable(), tradePanel.getTradeTableModel(), turnPanel.getTurnTableModel()));
        bar.add(tradesMenu);

        setJMenuBar(bar);
    }
}
