package cafe.josh.rsmm;/**
 * Created by joshua on 3/11/16.
 */

import cafe.josh.rsmm.viewfx.MMPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import cafe.josh.rsmm.viewfx.util.Dialogs;

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
            if(getParameters().getNamed().getOrDefault("ssl", "true").equalsIgnoreCase("false"))
            {
                conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/rsmm", "rsmm", "");
            }
            else if(getParameters().getNamed().getOrDefault("lax", "false").equalsIgnoreCase("true"))
            {
                conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/rsmm?ssl=true&sslfactory=org.postgresql.ssl.jdbc4.LibPQFactory", "rsmm", "");
            }
            else
            {
                conn = DriverManager.getConnection("jdbc:postgresql://" + host + "/rsmm?ssl=true&sslfactory=org.postgresql.ssl.jdbc4.LibPQFactory&sslmode=verify-full", "rsmm", "");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            Dialogs.showMessage("Database Error", "Error connecting to postgresql", ex);
            return;
        }

        stage.setScene(new Scene(new MMPane(conn)));
        stage.setTitle("RS Market Maker");
        stage.getIcons().add(new Image("/coins.png"));
        stage.show();
    }
}
