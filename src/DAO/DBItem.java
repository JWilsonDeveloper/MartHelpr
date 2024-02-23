package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;
import model.ItemPayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBItem {

    public static ObservableList<ItemPayer> addItems(ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Add items to database
        String sql = "INSERT INTO items (item_text, price) VALUES (?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        for (ItemPayer itemPayer : itemPayers) {
            ps.setString(1, itemPayer.getItem().getItemText());
            ps.setString(2, itemPayer.getItem().getPrice());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                // Get each item's auto-generated id so that it can be used to add to transaction_item_payers
                itemPayer.getItem().setItemId(rs.getInt(1));
            }
        }
        return itemPayers;
    }

    /*
    public static ObservableList<ItemPayer> getTransItemPayers(int transactionId) throws SQLException {
        ObservableList<ItemPayer> itemPayers = FXCollections.observableArrayList();

        // Get all item and payer ids for the transaction
        String sql = "SELECT item_id, payer_id FROM transaction_item_payers where transaction_id = ? GROUP BY item_id, payer_id";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(transactionId));
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get id for each item
            int itemID = rs.getInt("item_id");
            int payerID = rs.getInt("payer_id") - 1;

            // Add the itemPayer to a list
            Item item = new Item(itemID, null, null);
            ItemPayer itemPayer = new ItemPayer(item, String.valueOf(payerID));
            itemPayers.add(itemPayer);
        }

        ObservableList<ItemPayer> itemPayers2 = FXCollections.observableArrayList();
        // Combine itemPayers with the same item id
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
        String sql2 = "SELECT * FROM items where item_id = ?;";
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);
        for(ItemPayer ip2: itemPayers2) {
            ps2.setString(1, String.valueOf(ip2.getItem().getItemId()));
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()){
                // Get information for each item
                String itemText = rs2.getString("item_text");
                String price = rs2.getString("price");

                // Add item to a list
                ip2.getItem().setItemText(itemText);
                ip2.getItem().setPrice(price);
            }
        }

        return itemPayers2;
    }

     */



    public static ObservableList<ItemPayer> getItemsInfo(ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Get each item's information
        String sql = "SELECT * FROM items where item_id = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        for(ItemPayer ip: itemPayers) {
            ps.setString(1, String.valueOf(ip.getItem().getItemId()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                // Get information for each item
                String itemText = rs.getString("item_text");
                String price = rs.getString("price");

                // Add item to a list
                ip.getItem().setItemText(itemText);
                ip.getItem().setPrice(price);
            }
        }
        return itemPayers;
    }


    public static void updateItems(ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Add all the items to the database
        addItems(itemPayers);

        // Delete all items no longer associated with any transactions
        deleteLostItems();
    }

    public static void deleteLostItems() throws SQLException {
        // Delete all items no longer associated with any transactions
        String sql = "DELETE FROM items WHERE item_id NOT IN (SELECT item_id FROM transaction_item_payers)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.executeUpdate();
    }

    public static Double getCost(int itemId) throws SQLException {
        // Get the cost of an item
        Double price = null;
        String sql = "SELECT price FROM items WHERE item_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(itemId));
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            price = Double.parseDouble(rs.getString("price"));
        }
        price = Math.round(price * 100.0) / 100.0;

        return price;
    }

    public static ObservableList<Item> getAllItems() throws SQLException {
        String sql = "SELECT * FROM items";
        ObservableList<Item> items = FXCollections.observableArrayList();
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int itemId = rs.getInt("item_id");
            String itemText = rs.getString("item_text");
            String price = rs.getString("price");
            Item item = new Item(itemId, itemText, price);
            items.add(item);
        }
        return items;
    }
}
