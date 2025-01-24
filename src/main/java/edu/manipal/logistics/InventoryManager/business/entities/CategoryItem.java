package edu.manipal.logistics.InventoryManager.business.entities;

import com.google.cloud.datastore.Entity;

public class CategoryItem {
    private String categoryKey;
    private Long itemId;
    private Long requested;
    private Long given;

    public CategoryItem() {
        requested = 0L;
        given = 0L;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public void setRequested(Long requested) {
        this.requested = requested;
    }

    public void setGiven(Long given) {
        this.given = given;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getRequested() {
        return requested;
    }

    public Long getGiven() {
        return given;
    }

    public void setEntity(Entity ent) {
        setCategoryKey(ent.getString("categoryKey"));
        setItemId(ent.getLong("itemId"));
        setRequested(ent.getLong("requested"));
        setGiven(ent.getLong("given"));
    }
}
