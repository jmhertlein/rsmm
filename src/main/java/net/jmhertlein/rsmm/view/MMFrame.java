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

import net.jmhertlein.rsmm.controller.ShowItemManagerAction;
import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.model.QuoteManager;
import net.jmhertlein.rsmm.model.TurnManager;
import net.jmhertlein.rsmm.view.quote.QuotePanel;
import net.jmhertlein.rsmm.view.turn.TurnPanel;

/**
 * @author joshua
 */
public class MMFrame extends JFrame {
    private final ShowItemManagerAction showItemManagerAction;
    private final QuotePanel quotePanel;
    private final TurnPanel turnPanel;

    public MMFrame(Connection conn) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ItemManager items = new ItemManager(conn);
        QuoteManager quotes = new QuoteManager(conn);
        TurnManager turns = new TurnManager(conn);

        this.showItemManagerAction = new ShowItemManagerAction(items, this);
        this.quotePanel = new QuotePanel(items, quotes);
        quotePanel.setBorder(BorderFactory.createTitledBorder(""));

        this.turnPanel = new TurnPanel(turns, quotes, items);

        setupMenus();
        setupUI();
        setSize(800, 600);
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JSplitPane lrPanel = new JSplitPane(), rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        rightPanel.setTopComponent(new FatPanel(quotePanel));
        rightPanel.setBottomComponent(turnPanel);

        lrPanel.setRightComponent(rightPanel);

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
        setJMenuBar(bar);
    }
}
