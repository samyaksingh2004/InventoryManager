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

import ch.qos.logback.core.status.OnErrorConsoleStatusListener;

import com.google.cloud.datastore.StructuredQuery.CompositeFilter;


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

	public void deleteCategory(String name){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = Category.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("name" , name);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);	
			
			if(entities.hasNext()){
				Entity e = entities.next();
				datastore.delete(e.getKey());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
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
				set("requested" , ii.getRequested()).
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

	public void deleteInventoryItem(String itemKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = InventoryItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("itemKey" , itemKey);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);	
			
			if(entities.hasNext()){
				Entity e = entities.next();
				datastore.delete(e.getKey());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean existsInventoryItem(String itemKey){
		try{
			InventoryItem ii = getInventoryItem(itemKey);
			if(ii == null)
				return false;
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public void saveOrderItem(OrderItem oi){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = oi.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
			
			String compositeKey = oi.getCategoryKey() + "_" + oi.getItemKey();
			Key key = keyFactory.newKey(compositeKey);

        	Entity entity = Entity.newBuilder(key)
				.set("categoryKey", oi.getCategoryKey())
				.set("itemKey" , oi.getItemKey())
				.set("requested" , oi.getRequested())
				.set("given" , oi.getGiven())
				.build();

        	datastore.put(entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OrderItem getOrderItem(String itemKey , String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = OrderItem.class.getSimpleName();

			PropertyFilter itemKeyFilter = PropertyFilter.eq("itemKey", itemKey);
			PropertyFilter categoryKeyFilter = PropertyFilter.eq("categoryKey", categoryKey);
	
			CompositeFilter filter = CompositeFilter.and(itemKeyFilter, categoryKeyFilter);
	
			
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);

			if(entities.hasNext()){
				OrderItem oi = new OrderItem();
				oi.setEntity(entities.next());
				return oi;
			}
		}
		catch(Exception e){

		}

		return null;
	}

	public List<OrderItem> getAllItemsInCategory(String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = OrderItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("categoryKey", categoryKey);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);

			List<OrderItem> output = new ArrayList<OrderItem>();
			while(entities.hasNext()){
				OrderItem oi = new OrderItem();
				oi.setEntity(entities.next());
				output.add(oi);
			}

			return output;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public void deleteOrderItem(String itemKey , String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = OrderItem.class.getSimpleName();

			PropertyFilter itemKeyFilter = PropertyFilter.eq("itemKey", itemKey);
			PropertyFilter categoryKeyFilter = PropertyFilter.eq("categoryKey", categoryKey);
	
			CompositeFilter filter = CompositeFilter.and(itemKeyFilter, categoryKeyFilter);
	
			
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);	
			
			if(entities.hasNext()){
				Entity e = entities.next();
				datastore.delete(e.getKey());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}		
	}

	public Long getNumberOfItemRequested(String itemKey){
		// try to write a query to get the sum
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = OrderItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("itemKey", itemKey);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);

			Long count = 0L;
			while(entities.hasNext()){
				OrderItem oi = new OrderItem();
				oi.setEntity(entities.next());
				count += oi.getRequested();
			}

			return count;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return 0L;
	}

	
}
