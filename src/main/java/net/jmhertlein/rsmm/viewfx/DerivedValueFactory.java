package net.jmhertlein.rsmm.viewfx;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

/**
 * Created by joshua on 3/11/16.
 */
public class DerivedValueFactory<S,T> implements Callback<TableColumn.CellDataFeatures<S,T>,ObservableValue<T>> {
    @Override
    public ObservableValue<T> call(TableColumn.CellDataFeatures<S, T> features) {

    }
}
