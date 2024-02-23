package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ItemPayer;
import model.Transaction;
import utility.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DBTransaction {

    /*
    public static int addTransaction(String total, int numPayers, LocalDate transDate) throws SQLException {
        // Get current datetime and transaction datetime formatted in utc
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime createZdt = ZonedDateTime.now();
        ZonedDateTime createZdtUtc = Time.localToUTC(createZdt);
        String updated = createZdtUtc.format(formatter);

        String sql = "INSERT INTO transactions (total, no_payers, transaction_date, last_updated) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, total);
        ps.setString(2, String.valueOf(numPayers));
        ps.setString(3, String.valueOf(transDate));
        ps.setString(4, updated);

        int rowsAffected = ps.executeUpdate();
        int transactionId = -1;
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            transactionId = rs.getInt(1);
        }
        return transactionId;
    }

     */

    public static ObservableList<Transaction> getAllTransactions() throws SQLException {
        ObservableList<Transaction> allTransactions = FXCollections.observableArrayList();
        String sql = "SELECT * FROM transactions";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each transaction
            int transID = rs.getInt("transaction_id");
            String total = rs.getString("total");
            int numPayers = rs.getInt("no_payers");
            LocalDate transDate = rs.getDate("transaction_date").toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ZonedDateTime last_updated = ZonedDateTime.of(LocalDateTime.parse(rs.getString("last_updated"), formatter), ZoneId.of("UTC"));

            // Add transaction to a list
            Transaction transaction = new Transaction(transID, total, numPayers, transDate, last_updated);
            allTransactions.add(transaction);
        }
        return allTransactions;
    }

    public static int deleteTransaction(int transactionId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, transactionId);

        int rowsAffected = ps.executeUpdate();

        // Delete all of the transaction-item-payer relationships
        DBTransItemPayer.deleteTransItemPayer(transactionId);

        // Delete all items no longer associated with any transactions
        DBItem.deleteLostItems();
        return rowsAffected;
    }

    public static void updateTransaction(Transaction transaction, ObservableList<ItemPayer> itemPayers) throws SQLException {
        String sql = "UPDATE transactions SET total = ?, no_payers = ?, transaction_date = ?, last_updated = ? WHERE transaction_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, transaction.getTotal());
        ps.setString(2, String.valueOf(transaction.getNumPayers()));
        ps.setString(3, String.valueOf(transaction.getTransDate()));
        ps.setString(4, String.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        ps.setInt(5, transaction.getTransactionId());

        ps.executeUpdate();

        // Update the transaction-item-payer relationships in the database
        DBTransItemPayer.updateTransItemPayers(transaction.getTransactionId(), itemPayers);
        // Update the items in the database
        DBItem.updateItems(itemPayers);
    }

    public static void addTransaction(Transaction transaction, ObservableList<ItemPayer> itemPayers) throws SQLException {
        // Get current datetime and transaction datetime formatted in utc
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ZonedDateTime createZdt = ZonedDateTime.now();
        ZonedDateTime createZdtUtc = Time.localToUTC(createZdt);
        String updated = createZdtUtc.format(formatter);

        String sql = "INSERT INTO transactions (total, no_payers, transaction_date, last_updated) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, transaction.getTotal());
        ps.setString(2, String.valueOf(transaction.getNumPayers()));
        ps.setString(3, String.valueOf(transaction.getTransDate()));
        ps.setString(4, updated);

        ps.executeUpdate();
        int transactionId = -1;
        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()){
            transactionId = rs.getInt(1);
        }

        // Add the items to the database and get their auto-generated ids
        itemPayers = DBItem.addItems(itemPayers);

        // Add the transaction-item-payer relationships to the database
        DBTransItemPayer.addTransItemPayers(transactionId, itemPayers);
    }

    public static List<Integer> getTransactionIdsByDate(LocalDate date) throws SQLException {
        // Get a list of transactionIds that take place on or after this date
        List<Integer> transactionIds = new ArrayList<>();
        String sql = "SELECT transaction_id FROM transactions WHERE transaction_date >= ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(date));
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            transactionIds.add(rs.getInt("transaction_id"));
        }
        return transactionIds;
    }

    public static LocalDate getTransactionDate(int transactionId) throws SQLException {
        String sql = "SELECT transaction_date FROM transactions WHERE transaction_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, transactionId);
        ResultSet rs = ps.executeQuery();
        LocalDate date = null;
        if(rs.next()){
            date = rs.getDate("transaction_date").toLocalDate();
        }
        return date;
    }
}
