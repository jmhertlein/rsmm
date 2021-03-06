package cafe.josh.rsmm.viewfx.util;

import cafe.josh.rsmm.model.RSIntegers;
import javafx.util.StringConverter;

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
