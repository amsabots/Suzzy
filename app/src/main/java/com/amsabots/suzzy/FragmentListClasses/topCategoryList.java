package com.amsabots.suzzy.FragmentListClasses;

public class topCategoryList {
    private String categoryID, categoryname, imageurl;


    public topCategoryList() {
    }

    public topCategoryList(String categoryID, String categoryname, String imageurl) {
        this.categoryID = categoryID;
        this.categoryname = categoryname;
        this.imageurl = imageurl;
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
