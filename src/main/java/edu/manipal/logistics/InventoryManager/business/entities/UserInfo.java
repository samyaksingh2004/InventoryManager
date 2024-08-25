package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class UserInfo {
    private String name;
    private String password;

    public UserInfo(){
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getName(){
        return this.name;
    }

    public String getPassword(){
        return this.password;
    }

    public void setEntity(Entity ent){
        setName(ent.getString("name"));
        setPassword(ent.getString("password"));
    }
}
