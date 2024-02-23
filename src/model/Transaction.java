package model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class Transaction {

    private int transactionId;
    private String total;
    private int numPayers;
    private LocalDate transDate;
    private ZonedDateTime lastUpdated;

    public Transaction(int transactionId, String total, int numPayers, LocalDate transDate, ZonedDateTime lastUpdated) {
        this.transactionId = transactionId;
        this.total = total;
        this.numPayers = numPayers;
        this.transDate = transDate;
        this.lastUpdated = lastUpdated;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getNumPayers() {
        return numPayers;
    }

    public void setNumPayers(int numPayers) {
        this.numPayers = numPayers;
    }

    public LocalDate getTransDate() {
        return transDate;
    }

    public void setTransDate(LocalDate transDate) {
        this.transDate = transDate;
    }

    public ZonedDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(ZonedDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
