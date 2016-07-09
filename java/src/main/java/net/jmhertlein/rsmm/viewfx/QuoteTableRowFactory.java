package net.jmhertlein.rsmm.viewfx;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import net.jmhertlein.rsmm.model.Quote;

/**
 * Created by joshua on 5/14/16.
 */
public class QuoteTableRowFactory implements Callback<TableView<Quote>, TableRow<Quote>> {
    @Override
    public TableRow<Quote> call(TableView<Quote> val) {
        TableRow<Quote> row = new TableRow<>();

        ChangeListener<Boolean> syntheticPropertyListener = (prop, old, nu) -> {
            if (nu != null && nu) {
                row.setStyle("-fx-control-inner-background: aquamarine;\n" +
                        "  -fx-accent: derive(-fx-control-inner-background, -40%);\n" +
                        "  -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
            } else {
                row.setStyle("");
            }
        };

        row.itemProperty().addListener((prop, old, nu) -> {
            if (old != null) {
                //System.out.println("Removed listener from " + old.syntheticProperty().hashCode());
                old.syntheticProperty().removeListener(syntheticPropertyListener);
            }

            if (nu != null) {
                //System.out.println("Added listener to " + nu.syntheticProperty().hashCode());
                nu.syntheticProperty().addListener(syntheticPropertyListener);
                if (nu.isSynthetic()) {
                    row.setStyle("-fx-control-inner-background: aquamarine;\n" +
                            "  -fx-accent: derive(-fx-control-inner-background, -40%);\n" +
                            "  -fx-cell-hover-color: derive(-fx-control-inner-background, -20%);");
                } else {
                    row.setStyle("");
                }
            } else {
                row.setStyle("");
            }
        });

        return row;
    }
}
