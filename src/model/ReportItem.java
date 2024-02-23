package model;

import java.time.LocalDate;

public class ReportItem {

    private String itemText;
    private String itemPrice;
    private int timesPurchased;
    private LocalDate mostRecent;

    public ReportItem(String itemText, String itemPrice, int timesPurchased, LocalDate mostRecent) {
        this.itemText = itemText;
        this.itemPrice = itemPrice;
        this.timesPurchased = timesPurchased;
        this.mostRecent = mostRecent;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getTimesPurchased() {
        return timesPurchased;
    }

    public void setTimesPurchased(int timesPurchased) {
        this.timesPurchased = timesPurchased;
    }

    public LocalDate getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(LocalDate mostRecent) {
        this.mostRecent = mostRecent;
    }
}