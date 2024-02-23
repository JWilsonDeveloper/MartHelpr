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
import model.Payer;
import utility.SceneInterface;
import java.io.IOException;
import java.sql.SQLException;

public class PayerEditController {

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
    private TextArea editPayerMsgTxt;

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
    void onActionSave(ActionEvent event) throws SQLException, IOException {
        // Get payer name
        String name = payerNameTxt.getText();

        // Check if the name field is empty
        if(name.equals("")) {
            editPayerMsgTxt.setText("Please enter a name.");
        }
        else {
            // Update the payer's name
            int rowsAffected = DBPayer.updatePayer(Integer.parseInt(payerIDTxt.getText()) + 1, name);

            // If the update was successful, load the main menu
            if(rowsAffected == 1) {
                sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
            }
            // Else, the update failed...
            else {
                editPayerMsgTxt.setText("Error: Update failed.");
            }
        }
    }

    public void catchPayer(Payer payer) {
        // Set the payer's Id and name
        payerIDTxt.setText(String.valueOf(payer.getPayerId() - 1));
        payerNameTxt.setText(payer.getPayerName());
    }

}
