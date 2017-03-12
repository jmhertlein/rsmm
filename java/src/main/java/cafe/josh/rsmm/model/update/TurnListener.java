package cafe.josh.rsmm.model.update;

import cafe.josh.rsmm.model.Turn;

/**
 * Created by joshua on 4/9/16.
 */
public interface TurnListener {
    public void onTurnOpen(Turn t);
    public void onTurnClose(Turn t);
}
