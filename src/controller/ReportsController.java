package controller;

import DAO.DBItem;
import DAO.DBPayer;
import DAO.DBTransItemPayer;
import DAO.DBTransaction;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.*;
import utility.SceneInterface;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    // Declaration of variables
    Stage stage;
    Parent scene;
    ObservableList<ReportItem> reportItems = FXCollections.observableArrayList();

    // Implementation of SceneInterface
    SceneInterface sceneChange = (event, address) -> {
        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource(address));
        stage.setScene(new Scene(scene));
        stage.show();
    };

    @FXML
    private TableColumn<?, ?> itemMostRecentCol;

    @FXML
    private TableColumn<ReportItem, Integer> itemNoCol;

    @FXML
    private TableColumn<ReportItem, String> itemPriceCol;

    @FXML
    private TextField itemSearchTxt;

    @FXML
    private TableColumn<?, ?> itemTextCol;

    @FXML
    private TableColumn<?, ?> itemTimesPurchasedCol;

    @FXML
    private TableView<ReportItem> itemsTableView;

    @FXML
    private TableView<PayerItem> payersTableView;

    @FXML
    private TableColumn<PayerItem, Integer> payerIDCol;

    @FXML
    private TableColumn<PayerItem, String> payerNameCol;

    @FXML
    private TableColumn<PayerItem, Integer> payerNoItemsCol;

    @FXML
    private TextField payerSearchTxt;

    @FXML
    private TableColumn<PayerItem, String> payerTotalCol;

    @FXML
    private ToggleGroup payersTableTG;

    @FXML
    private RadioButton allTimeRBtn;

    @FXML
    private RadioButton sinceRBtn;

    @FXML
    private DatePicker sinceDP;

    @FXML
    private TextArea reportsMsgTxt;

    @FXML
    private BarChart<String,Number> transBarChart;

    @FXML
    void onActionAllTime(ActionEvent event) throws SQLException {
        // If the all time button is selected...
        if(allTimeRBtn.isSelected()) {
            // Disable the date picker and load the table with all payers
            sinceDP.setDisable(true);
            loadPayerTable(DBPayer.getAllPayers(0));

            // Enable the search field and clear the message text
            payerSearchTxt.setDisable(false);
            reportsMsgTxt.setText("");
        }
    }

    @FXML
    void onActionSince(ActionEvent event) {
        // If the since button is selected...
        if(sinceRBtn.isSelected()) {
            // Enable the date picker and clear the table
            sinceDP.setDisable(false);
            payersTableView.getItems().clear();

            // Disable the search field and clear the message text
            reportsMsgTxt.setText("Please select a date.");
            payerSearchTxt.setDisable(true);
        }
    }

    @FXML
    void onKeyPressItemSearch(KeyEvent event) throws SQLException {
        // If the enter key is pressed...
        if(event.getCode().getCode() == 10) {

            // Load the items table with all items and clear the message text
            itemsTableView.setItems(reportItems);
            reportsMsgTxt.setText("");

            // If the search field is not empty...
            if(!itemSearchTxt.getText().isEmpty()) {
                ObservableList<ReportItem> matchingItems = FXCollections.observableArrayList();
                try {
                    // Check if the search text is an integer matching an item number
                    int itemNum = Integer.parseInt(itemSearchTxt.getText());
                    for (ReportItem reportItem: reportItems) {
                        // If an item's index + 1 matches the search text, add it to the list
                        if (reportItems.indexOf(reportItem) == itemNum - 1) {
                            matchingItems.add(reportItem);
                            break;
                        }
                    }
                    if(matchingItems.size() == 0) {
                        throw new Exception();
                    }

                    // Display the matching item
                    itemsTableView.setItems(matchingItems);
                } catch (Exception e) {
                    // If the search text is not an integer, check each item's text for a match
                    for (ReportItem reportItem: reportItems) {
                        if (reportItem.getItemText().toLowerCase().contains(itemSearchTxt.getText().toLowerCase())) {
                            matchingItems.add(reportItem);
                        }
                    }

                    // If the search text does not match any item text...
                    if (matchingItems.isEmpty()) {
                        // Display a message
                        reportsMsgTxt.setText("No items matched your search.");
                    }
                    // Otherwise, filter the items table based on the search text
                    else {
                        itemsTableView.setItems(matchingItems);
                    }
                }
            }
        }
    }

    @FXML
    void onKeyPressPayerSearch(KeyEvent event) throws SQLException {
        // If the enter key is pressed...
        if(event.getCode().getCode() == 10) {
            // Load the payers table with all payers and clear the message text
            loadPayerTable(DBPayer.getAllPayers(0));
            reportsMsgTxt.setText("");

            // If the search field is not empty...
            if(!payerSearchTxt.getText().isEmpty()) {
                ObservableList<Payer> matchingPayers = FXCollections.observableArrayList();
                try {
                    // Payer IDs are displayed as 1 less than their actual value
                    int payerId = Integer.parseInt(payerSearchTxt.getText());
                    ++payerId;

                    // Check each payer for a matching ID
                    for (PayerItem payerItem : payersTableView.getItems()) {
                        if (payerItem.getPayer().getPayerId() == payerId) {
                            matchingPayers.add(payerItem.getPayer());
                        }
                    }
                    if(matchingPayers.size() == 0) {
                        throw new Exception();
                    }
                    // Display the matching payer
                    loadPayerTable(matchingPayers);

                } catch (Exception e) {
                    // If the search text is not an integer, check each payer's name for a match
                    for (PayerItem payerItem : payersTableView.getItems()) {
                        if (payerItem.getPayer().getPayerName().toLowerCase().contains(payerSearchTxt.getText().toLowerCase())) {
                            matchingPayers.add(payerItem.getPayer());
                        }
                    }

                    // If the search text does not match a payer name...
                    if (matchingPayers.isEmpty()) {
                        // Display a message
                        reportsMsgTxt.setText("No payers matched your search.");
                    }
                    // Otherwise, filter the payers table based on the search text
                    else {
                        loadPayerTable(matchingPayers);
                    }
                }
            }
        }
    }

    @FXML
    void onActionBack(ActionEvent event) throws IOException {
        // Load the main menu
        sceneChange.loadScene(event, "/view/MartHelprMenu.fxml");
    }

    private void loadPayerTable(ObservableList<Payer> payers){
        LocalDate date = null;
        // If the since button is selected...
        if (sinceRBtn.isSelected()) {
            // Get date from date picker
            date = sinceDP.getValue();
        }

        // Get information for payer table
        ObservableList<PayerItem> payerItems = FXCollections.observableArrayList();
        try {
            // Create a PayerItem for each payer
            for (Payer payer : payers) {
                PayerItem payerItem = new PayerItem(payer, null, 0);
                payerItems.add(payerItem);
            }

            // Get item IDs and costs for each payer
            for (PayerItem payerItem : payerItems) {
                // Get item IDs
                List<Integer> itemIds = DBTransItemPayer.getItemIdsByPayer(payerItem.getPayer().getPayerId(), date);

                // Get cost of each item
                for (int itemId : itemIds) {
                    // Get item's cost divided by its number of payers
                    Double itemCost = DBItem.getCost(itemId);
                    int numPayers = DBTransItemPayer.getNumPayers(itemId);
                    Double payerCost = itemCost / numPayers;
                    if(payerItem.getTotal() != null) {
                        payerCost += Double.parseDouble(payerItem.getTotal());
                    }
                    payerCost = Math.round(payerCost * 100.0) / 100.0;

                    // Set payer's total and number of items
                    payerItem.setTotal(payerCost.toString());
                    payerItem.setNumItems(payerItem.getNumItems() + 1);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Set payer table
        payersTableView.setItems(payerItems);
    }

    private ObservableList<ReportItem> getReportItems() throws SQLException {
        ObservableList<Item> items = DBItem.getAllItems();
        ObservableList<ReportItem> reportItems = FXCollections.observableArrayList();

        // Aggregate the items by name and price and add matches to the report items list
        for (Item item : items) {
            boolean isNew = true;
            LocalDate itemDate = DBTransItemPayer.getDateByItemId(item.getItemId());
            for (ReportItem reportItem : reportItems) {
                if (reportItem.getItemText().equals(item.getItemText()) && reportItem.getItemPrice().equals(item.getPrice())) {
                    isNew = false;
                    reportItem.setTimesPurchased(reportItem.getTimesPurchased() + 1);
                    if (itemDate.isAfter(reportItem.getMostRecent())) {
                        reportItem.setMostRecent(itemDate);
                    }
                    break;
                }
            }
            if (isNew) {
                ReportItem reportItem = new ReportItem(item.getItemText(), item.getPrice(), 1, itemDate);
                reportItems.add(reportItem);
            }
        }
        return reportItems;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the bar chart
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Transaction ID / Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Cost ($)");
        transBarChart.setTitle("Transaction Costs");
        try {
            ObservableList<Transaction> transactions = DBTransaction.getAllTransactions();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Transactions");
            for (Transaction transaction : transactions) {
                double total = Double.parseDouble(transaction.getTotal());
                series.getData().add(new XYChart.Data<>(transaction.getTransactionId() + "/" + transaction.getTransDate(), total));
            }
            transBarChart.getData().add(series);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }

        // Load the payer table
        try {
            loadPayerTable(DBPayer.getAllPayers(0));
            payerIDCol.setCellValueFactory(payerItemHolder -> new SimpleIntegerProperty(payerItemHolder.getValue().getPayer().getPayerId() - 1).asObject());
            payerNameCol.setCellValueFactory(payerItemHolder -> new SimpleStringProperty(payerItemHolder.getValue().getPayer().getPayerName()));
            payerNoItemsCol.setCellValueFactory(payerItemHolder -> new SimpleIntegerProperty(payerItemHolder.getValue().getNumItems()).asObject());
            payerTotalCol.setCellValueFactory(payerItemHolder -> {
                // If the payer has no total, display $0.00
                if(payerItemHolder.getValue().getTotal() == null) { return new SimpleStringProperty("$0.00"); }

                // If the payer has a total, display it with a dollar sign
                else { return new SimpleStringProperty("$" + payerItemHolder.getValue().getTotal()); }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // Create a listener to react when the user selects a date
        sinceDP.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    // Load the payer table with the selected date, enable the search field, and clear the message
                    loadPayerTable(DBPayer.getAllPayers(0));
                    payerSearchTxt.setDisable(false);
                    reportsMsgTxt.setText("");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });

        // Set the items table
        try {
            reportItems = getReportItems();
            itemsTableView.setItems(reportItems);
            itemNoCol.setCellValueFactory(reportItemHolder -> new SimpleIntegerProperty(reportItems.indexOf(reportItemHolder.getValue()) + 1).asObject());
            itemTextCol.setCellValueFactory(new PropertyValueFactory<>("itemText"));
            itemPriceCol.setCellValueFactory(reportItemHolder -> new SimpleStringProperty("$" + reportItemHolder.getValue().getItemPrice()));
            itemTimesPurchasedCol.setCellValueFactory(new PropertyValueFactory<>("timesPurchased"));
            itemMostRecentCol.setCellValueFactory(new PropertyValueFactory<>("mostRecent"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
