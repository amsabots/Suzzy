package com.example.suzzy.Cart;

public class CartList {
    private long price, number = 1 , millis= System.currentTimeMillis();
    private String unit, size, name, id, imageurl, tag, categoryid, desc;
    boolean loading;

    public CartList() {
    }

    public CartList(long price, long number, long millis, String unit, String size,
                    String name, String id, String imageurl, String tag,
                    String categoryid, String desc) {
        this.price = price;
        this.number = number;
        this.millis = millis;
        this.unit = unit;
        this.size = size;
        this.name = name;
        this.id = id;
        this.imageurl = imageurl;
        this.tag = tag;
        this.categoryid = categoryid;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }
}

