package net.jmhertlein.rsmm.view;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.Vector;

/**
 * Created by joshua on 2/2/16.
 */
public class ScalableJTable extends JTable {
    public ScalableJTable() {
        fixHeight();
    }

    public ScalableJTable(TableModel tableModel) {
        super(tableModel);
        fixHeight();
    }

    public ScalableJTable(TableModel tableModel, TableColumnModel tableColumnModel) {
        super(tableModel, tableColumnModel);
        fixHeight();
    }

    public ScalableJTable(TableModel tableModel, TableColumnModel tableColumnModel, ListSelectionModel listSelectionModel) {
        super(tableModel, tableColumnModel, listSelectionModel);
        fixHeight();
    }

    public ScalableJTable(int i, int i1) {
        super(i, i1);
        fixHeight();
    }

    public ScalableJTable(Vector vector, Vector vector1) {
        super(vector, vector1);
        fixHeight();
    }

    public ScalableJTable(Object[][] objects, Object[] objects1) {
        super(objects, objects1);
        fixHeight();
    }

    private void fixHeight() {
        setRowHeight(getFont().getSize() + 4);
    }
}
