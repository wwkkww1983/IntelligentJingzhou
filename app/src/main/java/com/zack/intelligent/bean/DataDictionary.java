package com.zack.intelligent.bean;

import java.util.List;
import java.util.Map;

/**
 * 数据字典列表
 */

public class DataDictionary {

    private String description;
    private boolean groupable;
    private String id;
//    private ItemsAsGroupBean itemsAsGroup;
    private Map<String, String> itemsAsMap;
    private String name;
    @com.google.gson.annotations.SerializedName("new")
    private boolean newX;
    private int version;
    private List<ItemsBean> items;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getItemsAsMap() {
        return itemsAsMap;
    }

    public void setItemsAsMap(Map<String, String> itemsAsMap) {
        this.itemsAsMap = itemsAsMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNewX() {
        return newX;
    }

    public void setNewX(boolean newX) {
        this.newX = newX;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public class ItemsBean{
        String label; //类型名称
        String value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ItemsBean{" +
                    "label='" + label + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
