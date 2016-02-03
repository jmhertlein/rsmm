/*
 * Copyright (C) 2016 joshua
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.jmhertlein.rsmm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.jmhertlein.rsmm.view.MMFrame;

/**
 * @author joshua
 */
public class RSMarketMaker {
    public static void main(String... args) {
        String host = "claudius";
        if (args.length > 0) {
            host = args[0];
        }


        if (System.getProperties().getProperty("os.name").toLowerCase().contains("linux")) {
            pickNimbus();
        } else {
            pickNative();
        }

        System.out.println("Connecting to " + host + ".");
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/rsmm", "rsmm", "");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error Connecting to Database", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MMFrame f = new MMFrame(conn);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    public static void pickNimbus() {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public static void pickNative() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
    }
}
