package model;

public class PayerItem {
    private Payer payer;
    private String total;
    private int numItems;

    public PayerItem(Payer payer, String total, int numItems) {
        this.payer = payer;
        this.total = total;
        this.numItems = numItems;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }
}
