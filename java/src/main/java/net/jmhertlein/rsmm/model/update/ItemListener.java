package net.jmhertlein.rsmm.model.update;

import net.jmhertlein.rsmm.model.Item;

/**
 * Created by joshua on 5/13/16.
 */
@FunctionalInterface
public interface ItemListener {
    public void onItemFavorited(Item e);
}
