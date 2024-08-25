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
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;


import edu.manipal.logistics.InventoryManager.business.entities.Category;
import edu.manipal.logistics.InventoryManager.business.entities.InventoryItem;
import edu.manipal.logistics.InventoryManager.business.entities.UserInfo;
import edu.manipal.logistics.InventoryManager.business.entities.CategoryItem;

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
				set("given" , ii.getGiven()).
				set("order" , ii.getOrder()).
				set("received" , ii.getReceived()).
				set("vendor" , ii.getVendor()).
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

	public void saveCategoryItem(CategoryItem ci){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = ci.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
			
			String compositeKey = ci.getCategoryKey() + "_" + ci.getItemKey();
			Key key = keyFactory.newKey(compositeKey);

        	Entity entity = Entity.newBuilder(key)
				.set("categoryKey", ci.getCategoryKey())
				.set("itemKey" , ci.getItemKey())
				.set("requested" , ci.getRequested())
				.set("given" , ci.getGiven())
				.build();

        	datastore.put(entity);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CategoryItem getCategoryItem(String itemKey , String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = CategoryItem.class.getSimpleName();

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
				CategoryItem ci = new CategoryItem();
				ci.setEntity(entities.next());
				return ci;
			}
		}
		catch(Exception e){

		}

		return null;
	}

	public List<CategoryItem> getAllItemsInCategory(String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = CategoryItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("categoryKey", categoryKey);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);

			List<CategoryItem> output = new ArrayList<CategoryItem>();
			while(entities.hasNext()){
				CategoryItem ci = new CategoryItem();
				ci.setEntity(entities.next());
				output.add(ci);
			}

			return output;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public void deleteCategoryItem(String itemKey , String categoryKey){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = CategoryItem.class.getSimpleName();

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

	public boolean existsCategoryItem(String itemKey , String categoryKey){
		try{
			CategoryItem ci = getCategoryItem(itemKey , categoryKey);
			if(ci == null)
				return false;
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public Long getNumberOfItemRequested(String itemKey){
		// try to write a query to get the sum
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = CategoryItem.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("itemKey", itemKey);
			Query<Entity> query = 
				Query.newEntityQueryBuilder()
						.setKind(kind)
						.setFilter(filter)
						.build();
			QueryResults<Entity> entities = datastore.run(query);

			Long count = 0L;
			while(entities.hasNext()){
				CategoryItem ci = new CategoryItem();
				ci.setEntity(entities.next());
				count += ci.getRequested();
			}

			return count;
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return 0L;
	}

	public void saveUserInfo(UserInfo ui){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = ui.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);

			Key key = keyFactory.newKey(ui.getName());

			Entity entity = Entity.newBuilder(key).
				set("name" , ui.getName()).
				set("password" , ui.getPassword()).
				build();

			datastore.put(entity);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public UserInfo getUserInfo(String name){
		try{
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = UserInfo.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("name", name);
			Query<Entity> query =
                Query.newEntityQueryBuilder()
                        .setKind(kind)
						.setFilter(filter)
                        .build();
        	QueryResults<Entity> entities = datastore.run(query);
			
			if(entities.hasNext()) {
				UserInfo ui = new UserInfo();
				ui.setEntity(entities.next());
				return ui;	
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}

	public boolean existsUser(String name){
		try{
			UserInfo ui = getUserInfo(name);

			if(ui == null)
				return false;
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

}
