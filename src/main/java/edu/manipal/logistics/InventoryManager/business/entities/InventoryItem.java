package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class InventoryItem {
    private String itemKey;
    private Long quantity;
    private Long requested;
    private Long given;
    private Long order;
    private Long received;
    private String vendor;

    public InventoryItem(){
        quantity = 0L;
        requested = 0L;
        given = 0L;
        order = 0L;
        received = 0L;
        vendor = "None";
    }

    public void setItemKey(String itemKey){
        this.itemKey = itemKey;
    }

    public void setQuantity(Long quantity){
        this.quantity = quantity;
    }

    public void setRequested(Long requested){
        this.requested = requested;
    }

    public void setGiven(Long given){
        this.given = given;
    }

    public void setOrder(Long order){
        this.order = order;
    }

    public void setReceived(Long received){
        this.received = received;
    }

    public void setVendor(String vendor){
        this.vendor = vendor;
    }

    public void changeQuantity(Long change){
        this.quantity += change;
    }

    public void changeRequested(Long change){
        this.requested += change;
    }

    public void changeReceived(Long change){
        this.quantity = this.quantity - this.received + change;
        this.received = change;
    }

    public void changeGiven(Long change){
        this.quantity -= change;
        this.given += change;
    }

    public String getItemKey(){
        return this.itemKey;
    }

    public Long getQuantity(){
        return this.quantity;
    }

    public Long getRequested(){
        return this.requested;
    }

    public Long getGiven(){
        return this.given;
    }

    public Long getOrder(){
        return this.order;
    }

    public Long getReceived(){
        return this.received;
    }

    public String getVendor(){
        return this.vendor;
    }

    public void setEntity(Entity ent){
        setItemKey(ent.getString("itemKey"));
        setQuantity(ent.getLong("quantity"));
        setRequested(ent.getLong("requested"));
        setGiven(ent.getLong("given"));
        setOrder(ent.getLong("order"));
        setReceived(ent.getLong("received"));
        setVendor(ent.getString("vendor"));
    }
}
