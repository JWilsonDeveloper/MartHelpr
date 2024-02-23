package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Payer;
import utility.Time;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DBPayer {

    public static ObservableList<Payer> getAllPayers(int code) throws SQLException {
        ObservableList<Payer> allPayers = FXCollections.observableArrayList();
        String sql = "SELECT * FROM payers";

        // If code is 1, exclude the first payer (id = 1)
        if(code == 1) {
            sql = "SELECT * FROM payers where payer_id != 1";
        }

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get information for each payer
            int payerID = rs.getInt("payer_id");

            String payerName = rs.getString("payer_name");
            ZonedDateTime created = null;
            if (rs.getString("created") != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                created = ZonedDateTime.of(LocalDateTime.parse(rs.getString("created"), formatter), ZoneId.of("UTC"));
            }

            // Add payer to a list
            Payer payer = new Payer(payerID, payerName, created);
            allPayers.add(payer);
        }
        return allPayers;
    }

    public static ObservableList<Payer> getTransPayers(int transactionId) throws SQLException {
        ObservableList<Payer> transPayers = FXCollections.observableArrayList();

        // Get all payer ids for the transaction
        String sql = "SELECT payer_id FROM transaction_item_payers where transaction_id = ? GROUP BY payer_id;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, String.valueOf(transactionId));
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            // Get id for each payer
            int payerID = rs.getInt("payer_id");

            // Add payer to a list
            Payer payer = new Payer(payerID, null, null);
            transPayers.add(payer);
        }

        // Get each payer's information
        String sql2 = "SELECT * FROM payers where payer_id = ?;";
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);
        for(Payer payer: transPayers) {
            ps2.setString(1, String.valueOf(payer.getPayerId()));
            ResultSet rs2 = ps2.executeQuery();
            while(rs2.next()){
                // Get information for each payer
                String payerName = rs2.getString("payer_name");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ZonedDateTime created = null;
                String createdString = rs2.getString("created");
                if(createdString != null) {
                    created = ZonedDateTime.of(LocalDateTime.parse(rs2.getString("created"), formatter), ZoneId.of("UTC"));
                }

                // Add payer to a list
                payer.setPayerName(payerName);
                payer.setCreated(created);
            }
        }
        return transPayers;
    }

    public static int addPayer(String name) throws SQLException {
        // Get current datetime formatted in utc
        ZonedDateTime createZdt = ZonedDateTime.now();
        ZonedDateTime createZdtUtc = Time.localToUTC(createZdt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String created = createZdtUtc.format(formatter);


        String sql = "INSERT INTO payers (payer_name, created) VALUES (?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, created);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public static int deletePayer(int payerId) throws SQLException {
        // Update all transaction-item-payer relationships with this payerId to payer_id = 1
        DBTransItemPayer.defaultPayer(payerId);

        // Delete payer from database
        String sql = "DELETE FROM payers WHERE payer_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, payerId);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public static int updatePayer(int payerId, String payerName) throws SQLException {
        String sql = "UPDATE payers SET payer_name = ? WHERE payer_id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, payerName);
        ps.setInt(2, payerId);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }
}
