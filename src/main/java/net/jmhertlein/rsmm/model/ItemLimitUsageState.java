package net.jmhertlein.rsmm.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by joshua on 5/13/16.
 */
public class ItemLimitUsageState {
    private final SimpleObjectProperty<Item> item;
    private final SimpleIntegerProperty limitLeft;

    public ItemLimitUsageState(Item i, int limitLeft) {
        item = new SimpleObjectProperty<>(i);
        this.limitLeft = new SimpleIntegerProperty(limitLeft);
    }

    public ObjectProperty<Item> itemProperty() {
        return item;
    }

    public IntegerProperty limitLeftProperty() {
        return limitLeft;
    }
}
