package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;
import model.ItemPayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBTransItemPayer {
    public static void addTransItemPayers(int transactionId, ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Add each transaction-item-payer relationship to the database
        for(ItemPayer itemPayer: itemPayers) {
            // Get the payer ids for the item
            String[] payerIds = itemPayer.getPayers().split("\\s+");
            // Insert each payer-item combination into the database
            for(String payerId: payerIds) {
                String sql = "INSERT INTO transaction_item_payers (transaction_id, item_id, payer_id) VALUES (?, ?, ?)";
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, String.valueOf(transactionId));
                ps.setString(2, String.valueOf(itemPayer.getItem().getItemId()));
                ps.setString(3, String.valueOf(Integer.parseInt(payerId)));
                ps.executeUpdate();
            }
        }
    }

    public static void updateTransItemPayers(int transactionId, ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Delete all transaction-item-payer relationships with this transaction id
        deleteTransItemPayer(transactionId);

        // Add the new payer-item combinations
        addTransItemPayers(transactionId, itemPayers);
    }

    public static ObservableList<ItemPayer> getTransItemPayers(int transactionId) throws SQLException {
        ObservableList<ItemPayer> itemPayers = FXCollections.observableArrayList();
        ObservableList<ItemPayer> itemPayers2 = FXCollections.observableArrayList();

        // Get all item and payer ids for the transaction and put them in itemPayers
        String sql = "SELECT item_id, payer_id FROM transaction_item_payers where transaction_id = ? GROUP BY item_id, payer_id";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(transactionId));
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get id for each item
            int itemID = rs.getInt("item_id");
            int payerID = rs.getInt("payer_id");

            // Add the itemPayer to a list
            Item item = new Item(itemID, null, null);
            ItemPayer itemPayer = new ItemPayer(item, String.valueOf(payerID));
            itemPayers.add(itemPayer);
        }

        // Combine itemPayers with the same item id, and put them in itemPayers2
        for(ItemPayer ip: itemPayers) {
            boolean foundMatch = false;
            for(ItemPayer ip2: itemPayers2) {
                if(ip.getItem().getItemId() == ip2.getItem().getItemId()) {
                    ip2.setPayers(ip2.getPayers() + " " + ip.getPayers());
                    foundMatch = true;
                    break;
                }
            }
            if(!foundMatch) {
                itemPayers2.add(ip);
            }
        }

        // Get each item's information
        itemPayers2 = DBItem.getItemsInfo(itemPayers2);

        return itemPayers2;
    }

    public static void deleteTransItemPayer(int transactionId) throws SQLException {
        // Delete all transaction-item-payer relationships with this transaction id
        String sql = "DELETE FROM transaction_item_payers WHERE transaction_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(transactionId));
        ps.executeUpdate();
    }

    public static void defaultPayer(int payerId) throws SQLException {
        // Update all transaction-item-payer relationships with this payerId to payer_id = 1
        String sql = "UPDATE transaction_item_payers SET payer_id = 1 WHERE payer_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(payerId));
        ps.executeUpdate();
    }

    public static List<Integer> getItemIdsByPayer(int payerId, LocalDate date) throws SQLException {
        List<Integer> transactionIds = new ArrayList<>();
        if(date != null) {
            // Get a list of transactionIds that take place on or after this date
            transactionIds = DBTransaction.getTransactionIdsByDate(date);
        }
        // Get all transaction-item-payer relationships with this payerItem's payer's payerId
        String sql = "SELECT * FROM transaction_item_payers WHERE payer_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(payerId));
        ResultSet rs = ps.executeQuery();
        List<Integer> itemIds = new ArrayList<>();
        while (rs.next()) {
            // Get item ids
            int itemID = rs.getInt("item_id");
            if(date != null) {
                // If the transaction id is not in the list of transaction ids, do not add it to itemIds
                int transactionId = rs.getInt("transaction_id");
                if (!transactionIds.contains(transactionId)) {
                    continue;
                }
            }
            itemIds.add(itemID);
        }

        return itemIds;
    }

    public static int getNumPayers(int itemId) throws SQLException {
        // Get the number of transaction-item-payer relationships with this item id
        String sql = "SELECT COUNT(*) FROM transaction_item_payers WHERE item_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(itemId));
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static LocalDate getDateByItemId(int itemId) throws SQLException {
        // Get the transaction id for this item id
        String sql = "SELECT transaction_id FROM transaction_item_payers WHERE item_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(itemId));
        ResultSet rs = ps.executeQuery();
        rs.next();
        int transactionId = rs.getInt("transaction_id");

        return DBTransaction.getTransactionDate(transactionId);
    }
}
