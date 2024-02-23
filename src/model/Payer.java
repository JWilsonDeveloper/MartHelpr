package model;

import java.time.ZonedDateTime;

public class Payer {
    private int payerId;
    private String payerName;
    private ZonedDateTime created;

    public Payer(int payerId, String payerName, ZonedDateTime created) {
        this.payerId = payerId;
        this.payerName = payerName;
        this.created = created;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
