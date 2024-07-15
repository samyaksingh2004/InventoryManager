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

public class GoogleDatastore {
    public void saveCategory(Category cl){
        try {
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = cl.getClass().getSimpleName();
			KeyFactory keyFactory = datastore.newKeyFactory().setKind(kind);
            
			Key key;

            if(cl.getCid() == 0l)
                key = datastore.allocateId(keyFactory.newKey());
            else
                key = keyFactory.newKey(cl.getCid());

        	Entity entity = Entity.newBuilder(key)
				.set("name", cl.getName())
				.build();

        	datastore.put(entity);
			cl.setCid(key.getId()); //
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
    }

    public Category getCategory(Long cid){
        try {
			Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
			String kind = Category.class.getSimpleName();

			PropertyFilter filter = StructuredQuery.PropertyFilter.eq("cid", cid);
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
			//throw e;
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
}
