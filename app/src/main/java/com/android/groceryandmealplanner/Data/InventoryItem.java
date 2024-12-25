package com.android.groceryandmealplanner.Data;

import com.google.firebase.Timestamp;

public class InventoryItem {

    private String id;
    private String name;
    private String expireDate;
    private String measurementUnit;
    private int quantity;
    private int lowStockAlert;
    private Timestamp lastUpdate;


    public InventoryItem() {}

    public InventoryItem(String id, String name, String expireDate, String measurementUnit, int quantity, int lowStockAlert, Timestamp lastUpdate) {
        this.id = id;
        this.name = name;
        this.expireDate = expireDate;
        this.measurementUnit = measurementUnit;
        this.quantity = quantity;
        this.lowStockAlert = lowStockAlert;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLowStockAlert() {
        return lowStockAlert;
    }

    public void setLowStockAlert(int lowStockAlert) {
        this.lowStockAlert = lowStockAlert;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
