package com.ravi.shoppinglist;

public class DataItem {
    private int serialNumber;
    private String data;
    private boolean isChecked;

    public int getSerialNumber() {
        return  serialNumber;
    }

    public String getData() {
        return data;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }
}
