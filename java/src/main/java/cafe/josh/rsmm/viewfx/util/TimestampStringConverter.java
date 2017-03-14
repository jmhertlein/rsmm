package cafe.josh.rsmm.viewfx.util;

import javafx.util.converter.FormatStringConverter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimestampStringConverter extends FormatStringConverter<Timestamp> {
    public TimestampStringConverter() {
        super(new SimpleDateFormat("HH:mm:ss"));
    }
}
