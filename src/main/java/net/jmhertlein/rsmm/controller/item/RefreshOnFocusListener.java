package net.jmhertlein.rsmm.controller.item;

import net.jmhertlein.rsmm.model.Item;
import net.jmhertlein.rsmm.model.ItemManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by joshua on 1/16/16.
 */
public class RefreshOnFocusListener<T> implements FocusListener {
    private final Component parent;
    private final Supplier<List<T>> supplier;
    private final Runnable clearExisting;
    private final Consumer<T> itemHandler;


    public RefreshOnFocusListener(Component parent, Supplier<List<T>> supplier, Runnable clearExisting, Consumer<T> itemHandler) {
        this.parent = parent;
        this.supplier = supplier;
        this.clearExisting = clearExisting;
        this.itemHandler = itemHandler;
    }

    @Override
    public void focusGained(FocusEvent focusEvent) {
        System.out.println("Focus gained.");
        clearExisting.run();
        for (T t : supplier.get()) {
            itemHandler.accept(t);
        }
    }

    @Override
    public void focusLost(FocusEvent focusEvent) {

    }
}
