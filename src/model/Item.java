package model;

public class Item {

    private int itemId;
    private String itemText;
    private String price;

public Item(int itemId, String itemText, String price) {
        this.itemId = itemId;
        this.itemText = itemText;
        this.price = price;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
