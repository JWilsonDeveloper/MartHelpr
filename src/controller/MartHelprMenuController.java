package controller;

import DAO.DBPayer;
import DAO.DBTransItemPayer;
import DAO.DBTransaction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import MartHelpr.Main;
import model.ItemPayer;
import model.Payer;
import model.Transaction;
import utility.*;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

public class MartHelprMenuController implements Initializable {

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
    private TableColumn<Payer, String> nameCol;

    @FXML
    private TableColumn<Payer, Integer> payerIDCol;

    @FXML
    private TableColumn<Payer, String> createdCol;

    @FXML
    private TableView<Payer> payersTableView;

    @FXML
    private TextArea martHelprMenuMsgTxt;

    @FXML
    private TableColumn<Transaction, Integer> noPayersCol;

    @FXML
    private TableColumn<Transaction, String> totalCol;

    @FXML
    private TableColumn<Transaction, LocalDate> transDateCol;

    @FXML
    private TableColumn<Transaction, Integer> transIDCol;

    @FXML
    private TableView<Transaction> transTableView;

    @FXML
    private TableColumn<Transaction, String> updateCol;

    @FXML
    void onActionCreatePayer(ActionEvent event) throws IOException {
        // Load PayerCreate scene
        sceneChange.loadScene(event, "/view/PayerCreate.fxml");
    }

    @FXML
    void onActionCreateTrans(ActionEvent event) throws IOException {
        // Load TransDetails scene
        sceneChange.loadScene(event, "/view/TransDetails.fxml");
    }

    @FXML
    void onActionDeleteTransaction(ActionEvent event) throws SQLException {
        try {
            // Get selected transaction
            Transaction transaction = transTableView.getSelectionModel().getSelectedItem();

            // Display confirmation message
            AtomicBoolean confirmBool = new AtomicBoolean(false);
            int transactionId =transaction.getTransactionId();
            Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            deleteConfirm.setContentText("Are you sure you want to delete this transaction?\nTransaction ID: " + transactionId);
            deleteConfirm.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> confirmBool.set(true));

            // If the user wants to delete the transaction...
            if(confirmBool.get()){
                // Call deteleTransaction to delete the transaction, its items, and its transaction-item-payer relationships from the database
                int rowsAffected = DBTransaction.deleteTransaction(transactionId);
                if(rowsAffected > 0) {
                    martHelprMenuMsgTxt.setText("Delete successful. A transaction has been removed from the table.\nTransaction ID: " + transactionId);
                }
                else {
                    martHelprMenuMsgTxt.setText("Delete failed");
                }

                // Reset transactions table in case a transaction has been deleted
                transTableView.setItems(DBTransaction.getAllTransactions());
            }
        }
        catch (NullPointerException e) {
            martHelprMenuMsgTxt.setText("Select a transaction from the table to delete.");
        }
    }

    @FXML
    void onActionDeletePayer(ActionEvent event) throws SQLException {
        try {
            // Get selected customer
            Payer payer = payersTableView.getSelectionModel().getSelectedItem();

            // Display confirmation message
            AtomicBoolean confirmBool = new AtomicBoolean(false);
            String payerName = payer.getPayerName();
            Alert deleteConfirm = new Alert(Alert.AlertType.CONFIRMATION);
            deleteConfirm.setContentText("Are you sure you want to delete this payer?\nPayer Name: " + payerName + "\n\nNote: All records of this payer in existing transactions will be marked as 'Deleted' with an id of 0.");
            deleteConfirm.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> confirmBool.set(true));

            // If the user wants to delete the payer...
            if (confirmBool.get()) {
                // Delete payer
                int rowsAffected = DBPayer.deletePayer(payer.getPayerId());
                if (rowsAffected > 0) {
                    martHelprMenuMsgTxt.setText("Delete successful. A payer has been removed from the table.\nPayer Name: " + payerName);
                } else {
                    martHelprMenuMsgTxt.setText("Delete failed");
                }

                // Reset payers table in case a payer has been deleted
                payersTableView.setItems(DBPayer.getAllPayers(1));
            }
        }
        catch (NullPointerException e) {
            martHelprMenuMsgTxt.setText("Select a payer from the table to delete.");
        }
    }


    @FXML
    void onActionEditPayer(ActionEvent event) throws IOException {
        try {
            // Load the payer edit layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/PayerEdit.fxml"));
            loader.load();

            // Pass the payer to the payer edit controller
            PayerEditController payerEditController = loader.getController();
            payerEditController.catchPayer(payersTableView.getSelectionModel().getSelectedItem());

            // Display the payer edit scene
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch (NullPointerException e) {
            martHelprMenuMsgTxt.setText("Select a payer from the table to edit.");
        }
    }

    @FXML
    void onActionEditTrans(ActionEvent event) throws IOException {
        Main.isExistingTrans = true;

        try {
            // Get the information of the selected transaction
            Transaction transaction = transTableView.getSelectionModel().getSelectedItem();
            int transId = transaction.getTransactionId();
            LocalDate transDate = transaction.getTransDate();
            ObservableList<Payer> allPayers = DBPayer.getTransPayers(transaction.getTransactionId());
            ObservableList<ItemPayer> itemPayers = DBTransItemPayer.getTransItemPayers(transaction.getTransactionId());

            // Load the TransSummary layout
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/TransSummary.fxml"));
            loader.load();

            // Pass the transaction's information to the summary controller
            TransSummaryController transSummaryController = loader.getController();
            transSummaryController.catchSummary(transDate, transId, null, allPayers, itemPayers);

            // Display the summary scene
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
        catch (NullPointerException e) {
            martHelprMenuMsgTxt.setText("Select a transaction from the table to edit.");
        }
        catch (SQLException e) {
            martHelprMenuMsgTxt.setText("Error: SQL Exception");
        }
    }

    @FXML
    void onActionLogOut(ActionEvent event) throws IOException {
        // Load the login scene
        sceneChange.loadScene(event, "/view/Login.fxml");
    }

    @FXML
    void onActionReports(ActionEvent event) throws IOException {
        // Load the reports scene
        sceneChange.loadScene(event, "/view/Reports.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Reset isExistingTrans in case a transaction edit was just closed
        Main.isExistingTrans = false;

        // Initialize payer table
        try {
            // Set payer table
            payersTableView.setItems(DBPayer.getAllPayers(1));
            payerIDCol.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
            nameCol.setCellValueFactory(new PropertyValueFactory<>("payerName"));
            createdCol.setCellValueFactory(payerHolder -> {
                // If the payer has a created date, format it
                if(payerHolder.getValue().getCreated() != null) { return new SimpleStringProperty(payerHolder.getValue().getCreated().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));}
                // Else, return null
                else { return null; }
            });

            // Set transaction table
            transTableView.setItems(DBTransaction.getAllTransactions());
            transIDCol.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
            totalCol.setCellValueFactory(transHolder -> new SimpleStringProperty("$" + transHolder.getValue().getTotal()));
            noPayersCol.setCellValueFactory(new PropertyValueFactory<>("numPayers"));
            transDateCol.setCellValueFactory(new PropertyValueFactory<>("transDate"));
            updateCol.setCellValueFactory(transHolder -> new SimpleStringProperty(transHolder.getValue().getLastUpdated().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
