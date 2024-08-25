package edu.manipal.logistics.InventoryManager.business.service;

import edu.manipal.logistics.InventoryManager.business.database.GoogleDatastore;
import edu.manipal.logistics.InventoryManager.business.entities.UserInfo;

public class LoginService {

    public LoginService(){
    }

    public boolean validateUser(String name , String password){
        GoogleDatastore gd = new GoogleDatastore();
        UserInfo ui = gd.getUserInfo(name);
        if(password.equals(ui.getPassword()))
            return true;
        
        return false;
        //return name.equals("admin") && password.equals("password");
    }
}
