package net.jmhertlein.rsmm.controller;

import javafx.collections.ObservableList;
import net.jmhertlein.rsmm.model.Turn;
import net.jmhertlein.rsmm.model.update.TurnListener;

/**
 * Created by joshua on 5/13/16.
 */
public class TurnTableTurnListener implements TurnListener {
    private final ObservableList<Turn> visibleTurns;

    public TurnTableTurnListener(ObservableList<Turn> visibleTurns) {
        this.visibleTurns = visibleTurns;
    }

    @Override
    public void onTurnOpen(Turn t) {
        visibleTurns.add(t);
    }

    @Override
    public void onTurnClose(Turn t) {
        visibleTurns.remove(t);
    }
}
