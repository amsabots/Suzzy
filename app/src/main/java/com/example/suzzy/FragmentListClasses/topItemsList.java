package com.example.suzzy.FragmentListClasses;

public class topItemsList {
    private String imageurl, categoryid, id, desc, name, unit, tag;
    long price;

    public topItemsList() {
    }

    public topItemsList(String imageurl, String categoryid, String id,
                        String desc, String name, String unit, String tag, long price) {
        this.imageurl = imageurl;
        this.categoryid = categoryid;
        this.id = id;
        this.desc = desc;
        this.name = name;
        this.unit = unit;
        this.tag = tag;
        this.price = price;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
