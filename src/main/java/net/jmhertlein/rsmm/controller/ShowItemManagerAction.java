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
package net.jmhertlein.rsmm.controller;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFrame;

import net.jmhertlein.rsmm.model.ItemManager;
import net.jmhertlein.rsmm.view.item.ItemManagerFrame;
import net.jmhertlein.rsmm.view.quote.QuotePanel;

/**
 * @author joshua
 */
public class ShowItemManagerAction extends AbstractAction {
    private final ItemManager items;
    private final JFrame parent;
    private final QuotePanel quotePanel;

    public ShowItemManagerAction(ItemManager items, JFrame parent, QuotePanel quotePanel) {
        super("Item Definitions...");
        this.items = items;
        this.parent = parent;
        this.quotePanel = quotePanel;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ItemManagerFrame f = new ItemManagerFrame(items, parent);
        f.setLocationRelativeTo(parent);
        f.setVisible(true);
        f.dispose();
    }

}
