package controller;

import DAO.DBTransaction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import MartHelpr.Main;
import model.*;
import utility.SceneInterface;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransSummaryController {

    // Declaration of variables
    Stage stage;
    Parent scene;
    File photoReceipt;
    LocalDate transactionDate;
    int transactionId;
    ObservableList<Payer> allPayers = FXCollections.observableArrayList();
    ObservableList<ItemPayer> itemPayers = FXCollections.observableArrayList();
    ObservableList<PayerItem> payerItems = FXCollections.observableArrayList();

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    @FXML
    private TextField dateTxt;

    @FXML
    private TextField totalTxt;

    @FXML
    private TableColumn<ItemPayer, Integer> itemCol;

    @FXML
    private TableColumn<ItemPayer, String> itemTextCol;

    @FXML
    private TableColumn<ItemPayer, String> priceCol;

    @FXML
    private TableColumn<ItemPayer, String> itemPayerIDCol;

    @FXML
    private TableView<ItemPayer> itemsTableView;

    @FXML
    private TableColumn<PayerItem, String> nameCol;

    @FXML
    private TableColumn<PayerItem, Integer> noItemsCol;

    @FXML
    private TableColumn<PayerItem, Integer> payerIDCol;

    @FXML
    private TableView<PayerItem> payersTableView;

    @FXML
    private TableColumn<PayerItem, String> totalCol;

    @FXML
    void onActionItems(ActionEvent event) throws IOException {
        // Load the items layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/TransItems.fxml"));
        loader.load();

        // Pass the transaction information to the items controller
        TransItemsController transItemsController= loader.getController();
        transItemsController.catchItems(transactionDate, transactionId, photoReceipt, allPayers, itemPayers);

        // Display the items scene
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionSave(ActionEvent event) throws IOException, SQLException {
        // Remove the $ from the total
        String formattedTotal = totalTxt.getText().replace("$", "");

        // Get the number of payers who had more than 0 items
        int numPayersWithItems = 0;
        for(PayerItem payerItem : payerItems) {
            if(payerItem.getNumItems() > 0) {
                numPayersWithItems++;
            }
        }

        // If the transaction is new...
        if(!Main.isExistingTrans) {
            // Call addTransaction to add the transaction, its items, and its transaction-item-payer relationships to the database
            Transaction transaction = new Transaction(0, formattedTotal, numPayersWithItems, transactionDate, null);
            DBTransaction.addTransaction(transaction, itemPayers);
        }
        // Else, the transaction already exists...
        else {
            // Call update transaction to update the transaction, its items, and its transaction-item-payer relationships in the database
            Transaction transaction = new Transaction(transactionId, formattedTotal, numPayersWithItems, transactionDate, null);
            DBTransaction.updateTransaction(transaction, itemPayers);
        }

        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    private ObservableList<PayerItem> getPayerItems(ObservableList<ItemPayer> itemPayers) {
        // Create a list of PayerItems to store each payer, their total cost, and the number of items they paid for
        for(Payer payer : allPayers) {
            payerItems.add(new PayerItem(payer, null, 0));
        }

        // Iterate through each itemPayer, divide the cost of its item between its payers, and add the divided cost to each of its payer's total
        for(ItemPayer ip: itemPayers) {
            // Get the payer ids for the item
            String[] payerIds = ip.getPayers().split("\\s+");

            // If there is only one payer id...
            if (payerIds.length == 1) {
                // Iterate through all the payers
                for (PayerItem pi : payerItems) {
                    // If the payer's id matches the item's payer id...
                    if (Integer.parseInt(payerIds[0]) == pi.getPayer().getPayerId()) {
                        // Add the cost of the item to the payer's total and add one to the number of items they paid for
                        Double total = Double.parseDouble(ip.getItem().getPrice());
                        if (pi.getTotal() != null) {
                            total += Double.parseDouble(pi.getTotal());
                        }
                        String totalString = String.format("%.2f", total);
                        pi.setTotal(totalString);
                        pi.setNumItems(pi.getNumItems() + 1);
                        break;
                    }
                }
            }
            // Else, there is more than one payer id for this item... split the cost of the item between the payers
            else {
                // Use costRemaining and payersRemaining to prevent rounding errors
                Double costRemaining = Double.parseDouble(ip.getItem().getPrice());
                int payersRemaining = payerIds.length;
                // Iterate through each of the item's payer ids
                for(String payerId: payerIds) {
                    // Iterate through all the payers
                    for(PayerItem pi: payerItems) {
                        // If the payer's id matches the item's payer id...
                        if(Integer.parseInt(payerId) == pi.getPayer().getPayerId()) {
                            // The cost remaining divided by the number of payers remaining will be added to the payer's total
                            Double payerPrice = costRemaining / payersRemaining;
                            payerPrice = Math.round(payerPrice * 100.0) / 100.0;

                            // Reduce the cost remaining by the cost added to the payer's total, and reduce the payers remaining by one
                            costRemaining -= payerPrice;
                            payersRemaining--;

                            // Add the divided cost to the payer's total and add one to the number of items they paid for
                            if(pi.getTotal() != null) {
                                payerPrice += Double.parseDouble(pi.getTotal());
                            }
                            String totalString = String.format("%.2f", payerPrice);
                            pi.setTotal(totalString);
                            pi.setNumItems(pi.getNumItems() + 1);
                            break;
                        }
                    }
                }
            }
        }

        return payerItems;
    }

    public void catchSummary(LocalDate transDate, int transactionId, File photoReceipt, ObservableList<Payer> allPayers, ObservableList<ItemPayer> itemPayers) {
        this.transactionDate = transDate;
        this.transactionId = transactionId;
        this.photoReceipt = photoReceipt;
        this.allPayers = allPayers;
        this.itemPayers = itemPayers;

        // Display the date
        dateTxt.setText(transDate.toString());

        // Set the items table
        itemsTableView.setItems(itemPayers);
        itemCol.setCellValueFactory(itemPayerHolder -> new SimpleIntegerProperty(itemPayers.indexOf(itemPayerHolder.getValue()) + 1).asObject());
        itemTextCol.setCellValueFactory(itemPayerHolder -> new SimpleStringProperty(itemPayerHolder.getValue().getItem().getItemText()));
        priceCol.setCellValueFactory(itemPayerHolder -> {
            // If the item has no price, display an empty cell
            if(itemPayerHolder.getValue().getItem().getPrice() == null) { return new SimpleStringProperty(itemPayerHolder.getValue().getItem().getPrice()); }

            // If the item has a price, display it with a dollar sign
            else { return new SimpleStringProperty("$" + itemPayerHolder.getValue().getItem().getPrice()); }
        });
        //itemPayerIDCol.setCellValueFactory(itemPayerHolder -> new SimpleStringProperty(itemPayerHolder.getValue().getPayers()));
        itemPayerIDCol.setCellValueFactory(itemPayerHolder -> {
            String oldString = itemPayerHolder.getValue().getPayers();
            // If the item has no payers, display an empty cell
            if (oldString == null) {
                return new SimpleStringProperty("");
            }
            // Else, the item has payers
            else {
                // Display the payer ids as their actual ids - 1
                String[] payerIdsStrings = oldString.split("\\s+");
                int[] payerIdsInts = new int[payerIdsStrings.length];
                for (int i = 0; i < payerIdsStrings.length; i++) {
                    payerIdsInts[i] = Integer.parseInt(payerIdsStrings[i]) - 1;
                }
                String newString = "";
                for (int i = 0; i < payerIdsInts.length; i++) {
                    newString += payerIdsInts[i];
                    if (i != payerIdsInts.length - 1) {
                        newString += " ";
                    }
                }
                return new SimpleStringProperty(newString);
            }
        });



        // Get payers, their totals and the number of items they paid for from itemPayers
        ObservableList<PayerItem> payerItems = getPayerItems(itemPayers);

        // Set the payers table
        payersTableView.setItems(payerItems);
        payerIDCol.setCellValueFactory(payerItemHolder -> new SimpleIntegerProperty(payerItemHolder.getValue().getPayer().getPayerId() - 1).asObject());
        nameCol.setCellValueFactory(payerItemHolder -> new SimpleStringProperty(payerItemHolder.getValue().getPayer().getPayerName()));
        totalCol.setCellValueFactory(payerItemHolder -> {
            // If the payer has no total, display $0.00
            if(payerItemHolder.getValue().getTotal() == null) { return new SimpleStringProperty("$0.00"); }
            // If the payer has a total, display it with a dollar sign
            else { return new SimpleStringProperty("$" + payerItemHolder.getValue().getTotal()); }
        });
        noItemsCol.setCellValueFactory(payerItemHolder -> new SimpleIntegerProperty(payerItemHolder.getValue().getNumItems()).asObject());

        // Calculate the total cost from each payer's total cost and display it
        Double total = 0.0;
        for (PayerItem pi: payerItems) {
            if (pi.getTotal() != null) {
                total += Double.parseDouble(pi.getTotal());
            }
        }
        total = Math.round(total * 100.0) / 100.0;
        totalTxt.setText("$" + String.format("%.2f", total));
    }
}
