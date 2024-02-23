package controller;

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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Item;
import model.ItemPayer;
import model.Payer;
import utility.ExtractPrice;
import utility.SceneInterface;
import utility.TesseractFunctions;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class TransItemsController {

    // Declaration of variables
    Stage stage;
    Parent scene;
    ObservableList<ItemPayer> itemPayers = FXCollections.observableArrayList();
    ObservableList<Payer> addedPayers = FXCollections.observableArrayList();
    ObservableList<Payer> allPayers = FXCollections.observableArrayList();
    ItemPayer currentRow;
    File photoReceipt;
    LocalDate transactionDate;
    int transactionId;

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    @FXML
    private Button finishBtn;

    @FXML
    private TextField dateTxt;

    @FXML
    private TextField itemNumTxt;

    @FXML
    private TextField costTxt;

    @FXML
    private TextField totalTxt;

    @FXML
    private TextArea transItemsMsgTxt;

    @FXML
    private TextArea itemTxt;

    @FXML
    private TableView<ItemPayer> itemsTableView;

    @FXML
    private TableColumn<ItemPayer, Integer> itemCol;

    @FXML
    private TableColumn<ItemPayer, String> itemTextCol;

    @FXML
    private TableColumn<ItemPayer, String> priceCol;

    @FXML
    private TableColumn<ItemPayer, String> itemPayerIDCol;

    @FXML
    private TableView<Payer> allPayersTableView;

    @FXML
    private TableColumn<Payer, Integer> payerIDCol;

    @FXML
    private TableColumn<Payer, String> nameCol;

    @FXML
    private TableView<Payer> addedPayersTableView;

    @FXML
    private TableColumn<Payer, Integer> payerIDCol1;

    @FXML
    private TableColumn<Payer, String> nameCol1;

    @FXML
    void onActionDetails(ActionEvent event) throws IOException {
        // Load the details layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/TransDetails.fxml"));
        loader.load();

        // Pass transaction information to the details controller
        TransDetailsController transDetailsController= loader.getController();
        transDetailsController.catchDetails(transactionDate, transactionId, photoReceipt, allPayers, itemPayers);

        // Display the details scene
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionSummary(ActionEvent event) throws IOException {
        // Load the summary layout
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/TransSummary.fxml"));
        loader.load();

        // Pass transaction information to the summary controller
        TransSummaryController transSummaryController = loader.getController();
        transSummaryController.catchSummary(transactionDate, transactionId, photoReceipt, allPayers, itemPayers);

        // Display the summary scene
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
    }

    @FXML
    void onActionCancel(ActionEvent event) throws IOException {
        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    @FXML
    void onActionAddNewItem(ActionEvent event) {
        // Create a new itemPayer
        ItemPayer newRow = new ItemPayer(new Item(0, null, null), null);

        // Add the new itemPayer to the list at the index of currentRow
        itemPayers.add(itemPayers.indexOf(currentRow), newRow);

        // Move to the new item and update the display
        currentRow = itemPayers.get(itemPayers.indexOf(currentRow) - 1);
        updateDisplay(currentRow);
    }

    @FXML
    void onActionDelete(ActionEvent event) {
        // Remove item from items list
        int deleteIndex = itemPayers.indexOf(currentRow);
        itemPayers.remove(deleteIndex);

        // If that was the last item on the list...
        if(itemPayers.size() == deleteIndex) {
            // Move to the previous item
            itemsTableView.getSelectionModel().select(deleteIndex - 1);
            currentRow = itemPayers.get(deleteIndex - 1);
        }
        // Else it was not the last item...
        else {
            // Move to the next item
            itemsTableView.getSelectionModel().select(deleteIndex);
            currentRow = itemPayers.get(deleteIndex);
        }

        // Update the display
        updateDisplay(currentRow);
    }

    @FXML
    void onActionPrev(ActionEvent event) {
        // Get previous index
        int prevIndex = itemPayers.indexOf(currentRow) - 1;

        // If there is a previous item...
        if(prevIndex >= 0) {
            // Move to the previous item and update the display
            currentRow = itemPayers.get(prevIndex);
            updateDisplay(currentRow);
        }
    }

    @FXML
    void onActionAccept(ActionEvent event) {
        // If the item has at least one payer...
        if(addedPayers.size() > 0) {
            // If a double can be parsed from the cost text...
            String cost = ExtractPrice.extractPrice(costTxt.getText());
            if (cost != null) {
                // Set the price of the item
                itemPayers.get(itemPayers.indexOf(currentRow)).getItem().setPrice(cost);

                // Set the payers of the item
                String payerIds = "";
                for (Payer payer : addedPayers) {
                    payerIds += payer.getPayerId() + " ";
                }
                itemPayers.get(itemPayers.indexOf(currentRow)).setPayers(payerIds);

                // Update the text of the item
                itemPayers.get(itemPayers.indexOf(currentRow)).getItem().setItemText(itemTxt.getText());

                // If this is not the last line...
                if (itemPayers.indexOf(currentRow) < itemPayers.size() - 1) {
                    // Move to the next line
                    currentRow = itemPayers.get(itemPayers.indexOf(currentRow) + 1);
                }

                // Update the display
                updateDisplay(currentRow);
            }
            // Else a double cannot be parse from the cost text...
            else {
                transItemsMsgTxt.setText("Please enter a valid cost");
            }
        }

        // Else the item has no payers...
        else{
            transItemsMsgTxt.setText("Please select at least one payer");
        }
    }

    private void updateDisplay(ItemPayer itemPayer) {
        // Update the item text and number
        String itemText = itemPayer.getItem().getItemText();
        itemTxt.setText(itemText);
        itemNumTxt.setText(String.valueOf(itemPayers.indexOf(currentRow) + 1));

        // Update the cost text
        // If the item already has an accepted price, display it
        if(itemPayer.getItem().getPrice() != null) {
            costTxt.setText("$" + itemPayer.getItem().getPrice());
        }
        // Else, the item does not have an accepted price...
        else {
            // Extract the price from the item text
            String extractedPrice = ExtractPrice.extractPrice(itemText);
            if(extractedPrice != null) {
                costTxt.setText("$" + extractedPrice);
            }
            else {
                costTxt.setText("");
            }
        }

        // Update the total price
        // Iterate through the rows in the table and add each item's price to the total if it's been accepted
        Double total = 0.0;
        for (ItemPayer ip: itemsTableView.getItems()) {
            if (ip.getItem().getPrice() != null) {
                total += Double.parseDouble(ip.getItem().getPrice());
            }
        }
        // Round the total to two decimal places
        total = Math.round(total * 100.0) / 100.0;
        totalTxt.setText("$" + String.format("%.2f", total));

        // Update the item payers table
        String payerIds = currentRow.getPayers();
        addedPayers.clear();
        // If the item has payers...
        if(payerIds != null) {
            // Split the payerIds String to get each payerId
            String[] payerIdArray = payerIds.split("\\s+");
            for (String payerId: payerIdArray) {
                for(Payer payer: allPayers) {
                    if(payer.getPayerId() == Integer.parseInt(payerId)) {
                        // Add the payer to the addedPayers list
                        addedPayers.add(payer);
                        break;
                    }
                }
            }
        }
        // Display the added Payers
        addedPayersTableView.setItems(addedPayers);

        // Set accepted items to green
        itemsTableView.setRowFactory(tableView -> new TableRow<ItemPayer>() {
            @Override
            public void updateItem(ItemPayer itemPayer, boolean empty) {
                super.updateItem(itemPayer, empty);
                // Only accepted items will have a price
                if (itemPayer != null && itemPayer.getItem().getPrice() != null) {
                    setStyle("-fx-background-color: lightgreen;");
                } else {
                    setStyle("");
                }
            }
        });

        // Select the current item, scroll to it, and refresh the table
        itemsTableView.getSelectionModel().select(itemPayers.indexOf(currentRow));
        itemsTableView.scrollTo(itemPayers.indexOf(currentRow));
        itemsTableView.refresh();

        // Select the first payer in the allPayers table and clear any messages
        allPayersTableView.getSelectionModel().select(0);
        transItemsMsgTxt.setText("");

        // Check if all items have been accepted (have a price)
        boolean finished = true;
        for (ItemPayer ip : itemPayers) {
            if (ip.getItem().getPrice() == null) {
                finished = false;
                break;
            }
        }
        // If all items have been accepted, enable the finish button
        if (finished) {
            finishBtn.setDisable(false);
        }
        else {
            finishBtn.setDisable(true);
        }
    }

    @FXML
    void onActionAddPayer(ActionEvent event) {
        // If a payer is selected...
        if (allPayersTableView.getSelectionModel().getSelectedItem() != null) {
            // If the payer has not already been added...
            if(!addedPayers.contains(allPayersTableView.getSelectionModel().getSelectedItem())) {
                // Add the item payer to the transaction's payers and reset the added payers table.
                addedPayers.add(allPayersTableView.getSelectionModel().getSelectedItem());
                addedPayersTableView.setItems(addedPayers);

                // If the added payer was not the last payer in allPayers...
                int addedIndex = allPayersTableView.getSelectionModel().getSelectedIndex();
                if (addedIndex < allPayersTableView.getItems().size() - 1) {
                    // Select the next payer in the allPayers table
                    allPayersTableView.getSelectionModel().select(addedIndex + 1);
                }

                // Select the added payer in the addedPayers table
                addedPayersTableView.getSelectionModel().select(addedPayers.size() - 1);
            }
        }
        // Else, no payer is selected...
        else {
            transItemsMsgTxt.setText("To add a payer, click on the payer and press add.");
        }
    }

    @FXML
    void onActionRemovePayer(ActionEvent event) {
        // If a payer is selected...
        if (addedPayersTableView.getSelectionModel().getSelectedItem() != null) {
            // Remove the payer from the added payers list, and reset the added payers table.
            addedPayers.remove(addedPayersTableView.getSelectionModel().getSelectedItem());
            addedPayersTableView.setItems(addedPayers);
        }
        // Else, no payer is selected...
        else {
            transItemsMsgTxt.setText("To remove a payer, click on the payer and press remove.");
        }
    }

    public void catchItems(LocalDate transDate, int transactionId, File photoReceipt, ObservableList<Payer> allPayers, ObservableList<ItemPayer> itemPayers) {
        this.transactionId = transactionId;
        this.transactionDate = transDate;
        this.photoReceipt = photoReceipt;
        this.allPayers = allPayers;
        this.itemPayers = itemPayers;

        // Set the allPayers table
        allPayersTableView.setItems(allPayers);
        payerIDCol.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
        nameCol.setCellValueFactory(new PropertyValueFactory<>("payerName"));
        allPayersTableView.getSelectionModel().select(0);

        // If itemPayers is empty...
        if (itemPayers.size() < 1) {
            // Extract the text from the receipt
            String[] textArray = TesseractFunctions.extractText(photoReceipt);

            // Create an itemPayer for each line of text
            for(int i = 0; i < textArray.length; ++i) {
                Item item = new Item(i + 1, textArray[i], null);
                ItemPayer itemPayer = new ItemPayer(item, null);
                itemPayers.add(itemPayer);
            }
        }

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

        // Set the added payers table
        addedPayersTableView.setItems(addedPayers);
        payerIDCol1.setCellValueFactory(payerHolder -> new SimpleIntegerProperty(payerHolder.getValue().getPayerId() - 1).asObject());
        nameCol1.setCellValueFactory(new PropertyValueFactory<>("payerName"));

        // Display the date
        dateTxt.setText(transDate.toString());

        // Select the first item payer that has no price, or the last item payer if all have prices
        boolean found = false;
        for(ItemPayer itemPayer : itemPayers) {
            if(itemPayer.getItem().getPrice() == null) {
                currentRow = itemPayer;
                found = true;
                break;
            }
        }
        if(!found){
            currentRow = itemPayers.get(itemPayers.size() - 1);
        }
        itemsTableView.getSelectionModel().select(itemPayers.indexOf(currentRow));
        updateDisplay(currentRow);
    }
}
