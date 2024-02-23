package model;

public class ItemPayer {
    private Item item;
    private String payers;

    public ItemPayer(Item item, String payers) {
        this.item = item;
        this.payers = payers;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public String getPayers() {
        return payers;
    }

    public void setPayers(String payers) {
        this.payers = payers;
    }
}
