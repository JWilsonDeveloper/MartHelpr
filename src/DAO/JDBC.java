package DAO;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public abstract class JDBC {

    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "marthelpr";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    public static Connection connection = null;  // Connection Interface

    public static void createDatabase() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(protocol + vendor + location, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/marthelpr.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            connection.close();
            System.out.println("Success: create database if not exists marthelpr");
        }
        catch(Exception e)
        {
            System.out.println("Error creating database:" + e.getMessage());
        }
    }

    public static void dropDatabase() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(protocol + vendor + location, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/drop_db.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            connection.close();
            System.out.println("Success: drop database if exists marthelpr");
        }
        catch(Exception e)
        {
            System.out.println("Error dropping database:" + e.getMessage());
        }
    }

    public static void createTables() {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            Statement statement = connection.createStatement();
            InputStream inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/items.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/payers.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/payers_reset_ai.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/payers_insert_data.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/transactions.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/transaction_payer_items.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/users.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/users_reset_ai.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));
            inputStream = JDBC.class.getClassLoader().getResourceAsStream("sql/users_insert_data.sql");
            statement.executeUpdate(new String(inputStream.readAllBytes()));

            connection.close();
            System.out.println("Success: create tables if not exist");
        }
        catch(Exception e)
        {
            System.out.println("Error creating tables:" + e.getMessage());
        }
    }

    public static Connection openConnection() {
        createDatabase();
        createTables();
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }

}
