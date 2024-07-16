package edu.manipal.logistics.InventoryManager.business.database;

import com.google.cloud.datastore.Entity;

import java.util.List;
import java.util.ArrayList;


import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import edu.manipal.logistics.InventoryManager.business.entities.Category;
import edu.manipal.logistics.InventoryManager.business.entities.InventoryItem;
import edu.manipal.logistics.InventoryManager.business.entities.OrderItem;

public class GoogleDatastore {
    public void saveCategory(Category c){
        try {
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = c.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
            
			Key key = keyFactory.newKey(c.getName());

        	Entity entity = Entity.newBuilder(key)
				.set("name", c.getName())
				.build();

        	datastore.put(entity);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }

    public Category getCategory(String name){
        try {
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = Category.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("name", name);
			Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(kind)
						.setFilter(filter)
                        .build();
        	QueryResults<Entity> entities = datastore.run(query);
			
			if(entities.hasNext()) {
				Category c = new Category();
				c.setEntity(entities.next());
				return c;	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    public List<Category> getAllCategory(){
        try{
            Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = Category.class.getSimpleName();

            Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(kind)
                        .build();
        	QueryResults<Entity> entities = datastore.run(query);

            List<Category> output = new ArrayList<Category>();
            while(entities.hasNext()) {
				Category c = new Category();
				c.setEntity(entities.next());
				output.add(c);	
			}

            return output;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

	public void saveInventoryItem(InventoryItem ii){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = ii.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);

			Key key = keyFactory.newKey(ii.getItemKey());

			Entity entity = Entity.newBuilder(key).
				set("itemKey" , ii.getItemKey()).
				set("quantity", ii.getQuantity()).
				build();

			datastore.put(entity);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public InventoryItem getInventoryItem(String itemKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = InventoryItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("itemKey", itemKey);
			Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(kind)
						.setFilter(filter)
                        .build();
        	QueryResults<Entity> entities = datastore.run(query);
			
			if(entities.hasNext()) {
				InventoryItem ii = new InventoryItem();
				ii.setEntity(entities.next());
				return ii;	
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}

	public List<InventoryItem> getAllInventoryItems(){
		try{
            Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = InventoryItem.class.getSimpleName();

            Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(kind)
                        .build();
        	QueryResults<Entity> entities = datastore.run(query);

            List<InventoryItem> output = new ArrayList<InventoryItem>();
            while(entities.hasNext()) {
				InventoryItem ii = new InventoryItem();
				ii.setEntity(entities.next());
				output.add(ii);
			}

            return output;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
	}
}
