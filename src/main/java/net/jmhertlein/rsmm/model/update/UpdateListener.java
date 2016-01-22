package net.jmhertlein.rsmm.model.update;

import java.sql.SQLException;

/**
 * Created by joshua on 1/21/16.
 */
@FunctionalInterface
public interface UpdateListener {
    public void onUpdate() throws SQLException;
}
