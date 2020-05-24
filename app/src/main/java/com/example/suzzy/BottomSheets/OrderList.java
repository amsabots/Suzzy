package com.example.suzzy.BottomSheets;

public class OrderList {
    private String id, status;
    private long amount, time;

    public OrderList() {
    }

    public OrderList(String id, String status, long amount, long time) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

