package com.jolly.imagecompression.models;

import android.webkit.JavascriptInterface;

public class InvoiceModel {
    private String imagePath;
    private String textStr;
    private String color;
    private String total;

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
}
