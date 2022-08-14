package com.example.demo.models;



public class CuentaModel {
    private long id;
    private boolean active_card;
    private int available_limit;

    public CuentaModel(CuentaModel that){
        this.id = that.id;
        this.active_card = that.active_card;
        this.available_limit = that.available_limit;
    }

    public CuentaModel(){
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getActive_card() {
        return active_card;
    }
    public void setActive_card(boolean active_card) {
        this.active_card = active_card;
    }
    public int getAvailable_limit() {
        return available_limit;
    }
    public void setAvailable_limit(int available_limit) {
        this.available_limit = available_limit;
    }
}
