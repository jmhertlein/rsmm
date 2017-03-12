package cafe.josh.rsmm.model.update;

import cafe.josh.rsmm.model.Item;

/**
 * Created by joshua on 5/13/16.
 */
@FunctionalInterface
public interface ItemListener {
    public void onItemFavorited(Item e);
}
