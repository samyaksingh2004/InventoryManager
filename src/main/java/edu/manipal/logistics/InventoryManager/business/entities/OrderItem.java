package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class OrderItem {
    private String categoryKey;
    private String itemKey;
    private Long quantity;
    private Long given;

    OrderItem(){
        quantity = 0L;
        given = 0L;
    }

    public void setCategoryKey(String categoryKey){
        this.categoryKey = categoryKey;
    }

    public void setItemKey(String itemKey){
        if(this.itemKey != null){

        }
        // create new inventoryItem
        this.itemKey = itemKey;
    }

    public void setQuantity(Long quantity){
        this.quantity = quantity;
    }

    public void setGiven(Long given){
        this.given = given;
    }

    public String getCategoryKey(){
        return categoryKey;
    }

    public String getItemKey(){
        return itemKey;
    }

    public Long getQuantity(){
        return quantity;
    }

    public Long getGiven(){
        return given;
    }

    public void setEntity(Entity ent){
        setCategoryKey(ent.getString("categoryKey"));
        setItemKey(ent.getString("itemKey"));
        setGiven(ent.getLong("quantity"));
    }
}
