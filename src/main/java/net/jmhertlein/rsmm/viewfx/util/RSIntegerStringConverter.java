package net.jmhertlein.rsmm.viewfx.util;

import javafx.util.StringConverter;
import net.jmhertlein.rsmm.model.RSIntegers;

/**
 * Created by joshua on 5/14/16.
 */
public class RSIntegerStringConverter extends StringConverter<Integer> {
    @Override
    public String toString(Integer object) {
        return RSIntegers.toString(object.intValue());
    }

    @Override
    public Integer fromString(String string) {
        return RSIntegers.parseInt(string);
    }
}
