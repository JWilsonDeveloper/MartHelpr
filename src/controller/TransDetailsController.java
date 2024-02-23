package controller;

import DAO.DBPayer;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import MartHelpr.Main;
import model.ItemPayer;
import model.Payer;
import utility.SceneInterface;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TransDetailsController implements Initializable {

    // Declaration of variables
    Stage stage;
    Parent scene;
    File photoReceipt;
    int transactionId;
    ObservableList<Payer> addedPayers = FXCollections.observableArrayList();
    ObservableList<ItemPayer> itemPayers = FXCollections.observableArrayList();

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    @FXML
    private TextArea transDetailsMsgTxt;

    @FXML
    private TableColumn<Payer, String> nameCol;

    @FXML
    private TableColumn<Payer, String> nameCol1;

    @FXML
    private TableColumn<Payer, Integer> payerIDCol;

    @FXML
    private TableColumn<Payer, Integer> payerIDCol1;

    @FXML
    private TableView<Payer> payersTableView;

    @FXML
    private TableView<Payer> payersTableView1;

    @FXML
    private TextArea photoReceiptTxt;

    @FXML
    private DatePicker transDP;

    @FXML
    private ImageView imageView;

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    @FXML
    void onActionItems(ActionEvent event) throws IOException {
        // If the transaction is new, initialize transactionId to 0.
        if(!Main.isExistingTrans) {
            transactionId = 0;
        }

        // Check for empty fields
        String eMsg = "";
        LocalDate transDate = transDP.getValue();
        if(transDate == null) {
            eMsg += "A transaction date is required\n";
        }
        if(photoReceipt == null && !Main.isExistingTrans) {
            eMsg += "A photo of your receipt is required for new transactions\n";
        }
        if(addedPayers.size() == 0) {
            eMsg += "At least one payer is required\n";
        }

        // If there are no empty fields...
        if(eMsg.equals("")) {
            // Load the items layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/TransItems.fxml"));
            loader.load();

            // Pass transaction information to the items controller
            TransItemsController transItemsController= loader.getController();
            transItemsController.catchItems(transDate, transactionId, photoReceipt, addedPayers, itemPayers);

            // Display the items scene
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        // Else, there are empty fields...
        else {
            transDetailsMsgTxt.setText(eMsg);
        }
    }

    @FXML
    void onActionAddPayer(ActionEvent event) {
        // If a payer is selected...
        if (payersTableView.getSelectionModel().getSelectedItem() != null) {
            // Check if the payer has already been added
            boolean alreadyAdded = false;
            for(Payer payer: addedPayers) {
                if(payer.getPayerId() == payersTableView.getSelectionModel().getSelectedItem().getPayerId()) {
                    alreadyAdded = true;
                }
            }
            if(!alreadyAdded) {
                // Add the selected payer to the transaction's payers and reset the added payers table.
                addedPayers.add(payersTableView.getSelectionModel().getSelectedItem());
                payersTableView1.setItems(addedPayers);
                payerIDCol1.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
                nameCol1.setCellValueFactory(new PropertyValueFactory<>("payerName"));
                // Manage selected rows
                int addedIndex = payersTableView.getSelectionModel().getSelectedIndex();
                if (addedIndex < payersTableView.getItems().size() - 1) {
                    payersTableView.getSelectionModel().select(addedIndex + 1);
                }
                payersTableView1.getSelectionModel().select(addedPayers.size() - 1);
            }
        }
        // Else, no payer is selected...
        else {
            transDetailsMsgTxt.setText("To add a payer, click on the payer and press add.");
        }
    }

    @FXML
    void onActionRemovePayer(ActionEvent event) {
        // If a payer is selected...
        if (payersTableView1.getSelectionModel().getSelectedItem() != null) {
            // Clear the payers for each itemPayers to prevent the items page from displaying removed payers.
            for(ItemPayer itemPayer: itemPayers) {
                itemPayer.setPayers(null);
            }

            // Remove the payer from the added payers list, and reset the selected payers table.
            addedPayers.remove(payersTableView1.getSelectionModel().getSelectedItem());
            payersTableView1.setItems(addedPayers);
        }
        // Else, no payer is selected...
        else {
            transDetailsMsgTxt.setText("To remove a payer, click on the payer and press remove.");
        }
    }

    @FXML
    void onActionUpload(ActionEvent event) {
        // Clear the itemPayers list so that text will be extracted from the new receipt
        itemPayers.clear();

        // Upload and store a photo of the receipt
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        photoReceipt = fileChooser.showOpenDialog(new Stage());

        // If the photo has been successfully stored...
        if(photoReceipt != null) {
            // Display the photo's path and the photo
            photoReceiptTxt.setText(photoReceipt.getAbsolutePath());
            Image image = new Image(photoReceipt.toURI().toString());
            imageView.setImage(image);
        }
    }

    public void catchDetails(LocalDate transDate, int transactionId, File photoReceipt, ObservableList<Payer> addedPayers, ObservableList<ItemPayer> itemPayers) {
        // Set the itemPayers
        this.itemPayers = itemPayers;

        // Set the transaction ID
        this.transactionId = transactionId;

        // Set the transaction date
        transDP.setValue(transDate);

        // Set the photo receipt
        if(photoReceipt != null) {
            this.photoReceipt = photoReceipt;
            photoReceiptTxt.setText(photoReceipt.getAbsolutePath());
            Image image = new Image(photoReceipt.toURI().toString());
            imageView.setImage(image);
        }

        // Set the added payers
        this.addedPayers = addedPayers;
        payersTableView1.setItems(addedPayers);
        payerIDCol1.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
        nameCol1.setCellValueFactory(new PropertyValueFactory<>("payerName"));

        // Warn the user that progress will be lost if they change the receipt or remove a payer
        transDetailsMsgTxt.setText("Warning: Uploading a new receipt or removing an added payer on this page will clear any changes made to the items page.");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Set the payers table
            payersTableView.setItems(DBPayer.getAllPayers(1));
            payerIDCol.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
            nameCol.setCellValueFactory(new PropertyValueFactory<>("payerName"));

        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Select the first payer
        payersTableView.getSelectionModel().select(0);
    }
}