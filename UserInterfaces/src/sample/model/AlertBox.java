package sample.model;

import javafx.scene.control.Alert;

/**
 * Created by Yves on 10/20/2016.
 */
public class AlertBox {

    public static void display(String title, String header, String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        alert.showAndWait();
    }
}
