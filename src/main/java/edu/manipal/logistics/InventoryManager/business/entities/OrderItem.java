package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

import edu.manipal.logistics.InventoryManager.business.database.GoogleDatastore;

public class OrderItem {
    private String categoryKey;
    private String itemKey;
    private Long requested;
    private Long given;

    public OrderItem(){
        requested = 0L;
        given = 0L;
    }

    public void setCategoryKey(String categoryKey){
        this.categoryKey = categoryKey;
    }

    public void setItemKey(String itemKey){
        GoogleDatastore gd = new GoogleDatastore();
        if(!gd.existsInventoryItem(itemKey)){
            InventoryItem ii = new InventoryItem();
            ii.setItemKey(itemKey);
            gd.saveInventoryItem(ii);
        }
        // create new inventoryItem

        this.itemKey = itemKey;
    }

    public void setRequested(Long requested){
        if(itemKey == "")
            return;
            
        GoogleDatastore gd = new GoogleDatastore();
        InventoryItem ii = gd.getInventoryItem(itemKey);
        ii.changeRequested(requested);
        gd.saveInventoryItem(ii);
        this.requested = requested;
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

    public Long getRequested(){
        return requested;
    }

    public Long getGiven(){
        return given;
    }

    public void setEntity(Entity ent){
        setCategoryKey(ent.getString("categoryKey"));
        setItemKey(ent.getString("itemKey"));
        setRequested(ent.getLong("requested"));
        setGiven(ent.getLong("given"));
    }
}
