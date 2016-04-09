package net.jmhertlein.rsmm.viewfx;

import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import net.jmhertlein.rsmm.model.Trade;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by joshua on 3/11/16.
 */
public class RepopulateTradesListener implements UpdateListener {
    private final ObservableList<Trade> shownTrades;
    private final TableView<Turn> turnTable;

    public RepopulateTradesListener(ObservableList<Trade> shownTrades, TableView<Turn> turnTable) {
        this.shownTrades = shownTrades;
        this.turnTable = turnTable;
    }

    @Override
    public void onUpdate() throws SQLException {
        Optional.ofNullable(turnTable.getSelectionModel().getSelectedItem()).ifPresent(turn -> {
            try {
                shownTrades.setAll(turn.getTrades());
            } catch (SQLException e) {
                Dialogs.showMessage("Trade Reload Error", "Error reloading trades for turn " + turn.getItemName(), e);
            }
        });
    }
}
