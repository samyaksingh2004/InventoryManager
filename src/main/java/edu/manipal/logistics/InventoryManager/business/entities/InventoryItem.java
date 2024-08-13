package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class InventoryItem {
    private String itemKey;
    private Long quantity;
    private Long requested;

    public InventoryItem(){
        quantity = 0L;
        requested = 0L;
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

    public void changeRequested(Long change){
        this.requested += change;
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

    public void setEntity(Entity ent){
        setItemKey(ent.getString("itemKey"));
        setQuantity(ent.getLong("quantity"));
        setRequested(ent.getLong("requested"));
    }
}
