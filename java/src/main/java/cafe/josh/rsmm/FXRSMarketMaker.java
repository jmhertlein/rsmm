package cafe.josh.rsmm;

import cafe.josh.joshfx.Dialogs;
import cafe.josh.rsmm.model.RSType;
import cafe.josh.rsmm.viewfx.MMPane;
import cafe.josh.rsmm.viewfx.RSTypeDialog;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

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

        RSTypeDialog dlg = new RSTypeDialog();
        Optional<RSType> o = dlg.showAndWait();
        if(!o.isPresent())
        {
            System.out.println("No choice picked, exiting.");
            return;
        }

        System.out.println("RSType: " + o.get().getEnumString());

        stage.setScene(new Scene(new MMPane(conn, o.get())));
        stage.setTitle("RS Market Maker");
        stage.getIcons().add(new Image("/coins.png"));
        stage.show();
    }
}
