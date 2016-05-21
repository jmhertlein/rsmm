package net.jmhertlein.rsmm.viewfx.util;

import javafx.util.StringConverter;
import net.jmhertlein.rsmm.model.RSIntegers;

/**
 * Created by joshua on 5/14/16.
 */
public class RSNumberStringConverter extends StringConverter<Number> {
    @Override
    public String toString(Number object) {
        return RSIntegers.toString(object.intValue());
    }

    @Override
    public Number fromString(String string) {
        return RSIntegers.parseInt(string);
    }
}
