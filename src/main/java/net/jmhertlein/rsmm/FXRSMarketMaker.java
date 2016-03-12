package net.jmhertlein.rsmm;/**
 * Created by joshua on 3/11/16.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.jmhertlein.rsmm.viewfx.MMPane;
import net.jmhertlein.rsmm.viewfx.util.Dialogs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FXRSMarketMaker extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        String host = getParameters().getNamed().getOrDefault("host", "claudius");

        System.out.println("Connecting to " + host + ".");
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/rsmm", "rsmm", "");
        } catch (SQLException ex) {
            Dialogs.showMessage("Database Error", "Error connecting to postgresql", ex);
            return;
        }

        stage.setTitle("RS Market Maker");
        stage.setScene(new Scene(new MMPane(conn)));
        stage.show();
    }
}
