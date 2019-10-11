package com.jolly.imagecompression.models;

import android.webkit.JavascriptInterface;

public class ContentData {
    private String name;
    private String quantity;
    private String price;

    @JavascriptInterface
    public String getQuantity(){
        return quantity;
    }

    public void setQuantity(String quantity){
        this.quantity = quantity;
    }

    public void setPrice(String price){
        this.price = price;
    }

    @JavascriptInterface
    public String getPrice(){
        return price;
    }

    @JavascriptInterface
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
}
