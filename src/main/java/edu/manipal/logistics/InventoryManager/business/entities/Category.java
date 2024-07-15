package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class Category {
    private Long cid;
    private String name;

    public Category(){
        cid = 0L;
    }

    public void setCid(Long cid){
        this.cid = cid;
    }

    public void setName(String name){
        this.name = name;
    }

    public Long getCid(){
        return this.cid;
    }

    public String getName(){
        return this.name;
    }

    public void setEntity(Entity ent){
        //setCid(ent.);
        setCid(ent.getKey().getId());
        setName(ent.getString("name"));
    }
}
