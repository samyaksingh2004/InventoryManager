package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class InventoryItem {
    private String itemKey;
    private Long quantity;

    public InventoryItem(){
        quantity = 0L;
    }

    public void setItemKey(String itemKey){
        this.itemKey = itemKey;
    }

    public void setQuantity(Long quantity){
        this.quantity = quantity;
    }

    public String getItemKey(){
        return itemKey;
    }

    public Long getQuantity(){
        return this.quantity;
    }

    public void setEntity(Entity ent){
        setItemKey(ent.getString("itemKey"));
        setQuantity(ent.getLong("quantity"));
    }
}
