package controller;

import DAO.DBPayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utility.SceneInterface;
import java.io.IOException;
import java.sql.SQLException;

public class PayerCreateController {

    // Declaration of variables
    Stage stage;
    Parent scene;

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    @FXML
    private TextArea createPayerMsgTxt;

    @FXML
    private TextField payerIDTxt;

    @FXML
    private TextField payerNameTxt;

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    @FXML
    void onActionSave(ActionEvent event) throws IOException, SQLException {
        // Get the name
        String name = payerNameTxt.getText();

        // Check if the name field is empty
        if(name.equals("")) {
            createPayerMsgTxt.setText("Please enter a name.");
        }
        else {
            // Add the payer to the database
            int rowsAffected = DBPayer.addPayer(name);
            // If the payer was added, load the main menu
            if(rowsAffected == 1) {
                sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
            }
            // Else, the update failed...
            else {
                createPayerMsgTxt.setText("Payer not added.");
            }
        }
    }
}
