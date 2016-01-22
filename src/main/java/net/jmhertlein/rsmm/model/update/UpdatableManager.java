package net.jmhertlein.rsmm.model.update;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by joshua on 1/21/16.
 */
public abstract class UpdatableManager {
    private final List<UpdateListener> listeners;

    protected UpdatableManager() {
        listeners = new LinkedList<>();
    }

    public void addListener(UpdateListener l) {
        listeners.add(l);
    }

    public void fireUpdateEvent() throws SQLException {
        for (UpdateListener l : listeners) {
            l.onUpdate();
        }
    }
}
