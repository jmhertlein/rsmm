package cafe.josh.rsmm.viewfx.util;

import cafe.josh.rsmm.model.RSIntegers;
import javafx.util.StringConverter;

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
