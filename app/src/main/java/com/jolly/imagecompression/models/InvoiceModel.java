package com.jolly.imagecompression.models;

import android.webkit.JavascriptInterface;

import java.util.ArrayList;

public class InvoiceModel {
    private String imagePath;
    private String textStr;
    private String color;
    private String total;
    private ArrayList<ContentData> list = new ArrayList<>();

    @JavascriptInterface
    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @JavascriptInterface
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @JavascriptInterface
    public String getTextStr() {
        return textStr;
    }

    public void setTextStr(String textStr) {
        this.textStr = textStr;
    }
    @JavascriptInterface
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setList(ArrayList<ContentData> list) {
        this.list = list;
    }

    public ArrayList<ContentData> getList() {
        return list;
    }

    public class ContentData {
        private String name;
        private String quantity;
        private String price;

        public ContentData(String name,String quantity,String price){
            this.name=name;
            this.quantity=quantity;
            this.price=price;
        }

        @JavascriptInterface
        public String getQuantity(){
            return quantity;
        }


        @JavascriptInterface
        public String getPrice(){
            return price;
        }

        @JavascriptInterface
        public String getName(){
            return name;
        }

    }
}
