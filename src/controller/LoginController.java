package controller;

import DAO.DBUser;
import DAO.JDBC;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import utility.SceneInterface;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import com.sun.jna.Native;

public class LoginController {

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
    private Button exitBtn;

    @FXML
    private TextField loginMsgTxt;

    @FXML
    private Label passwordLbl;

    @FXML
    private TextField passwordTxt;

    @FXML
    private Button submitBtn;

    @FXML
    private Label usernameLbl;

    @FXML
    private TextField usernameTxt;

    @FXML
    void onActionExit(ActionEvent event) {
        System.out.println("JNA Version: " + Native.VERSION);

        String classpath = System.getProperty("java.class.path");
        System.out.println("Classpath: " + classpath);
        JDBC.closeConnection();
        System.exit(0);
    }

    @FXML
    void onActionSubmit(ActionEvent event) throws SQLException, IOException {
        // Check if the username or password fields are empty.
        String username = usernameTxt.getText();
        String password = passwordTxt.getText();
        if(username.equals("") || password.equals("")) {
            loginMsgTxt.setText("Please enter a username and password");
        }
        else {
            // Output file name
            String filename = "login_activity.txt";

            // The message that will be written to the file
            String outputLine;

            // Create fileWriter object
            FileWriter fWriter = new FileWriter(filename, true);

            // Create and open file
            PrintWriter outputFile = new PrintWriter(fWriter);

            try {
                // Get username
                User user = DBUser.getUser(username);

                // Throw an exception if the user doesn't exist or the password is incorrect
                if (user == null || !user.getPassword().equals(password)) {
                    throw new Exception();
                }

                // Write to file
                outputLine = "User " + username + " successfully logged in at " + LocalDateTime.now(ZoneOffset.UTC);
                outputFile.println(outputLine);
                outputFile.close();

                // Load the main menu
                sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
            }
            catch (Exception e) {
                outputLine = "User " + username + " gave invalid login at " + LocalDateTime.now(ZoneOffset.UTC);
                outputFile.println(outputLine);
                outputFile.close();
                loginMsgTxt.setText("Invalid Username/Password");
            }
        }
    }
}