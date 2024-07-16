package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class Category {
    private String name;

    public Category(){
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public void setEntity(Entity ent){
        setName(ent.getString("name"));
    }
}
