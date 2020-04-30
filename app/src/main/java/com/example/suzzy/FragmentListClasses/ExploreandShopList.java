package com.example.suzzy.FragmentListClasses;

public class ExploreandShopList {
    private String categoryID, categoryname, imageurl;

    public ExploreandShopList(String categoryID, String categoryname, String imageurl) {
        this.categoryID = categoryID;
        this.categoryname = categoryname;
        this.imageurl = imageurl;
    }

    public ExploreandShopList() {
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
