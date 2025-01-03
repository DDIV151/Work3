package com.ddiv.oms;

import java.util.Date;

public class Item {
    private int itemId;
    private String name;
    private int price;
    private int stock;
    private String unit = "个";
    private int isDelete;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public Item() {
    }

    public Item(int itemId, String name, int price, int stock, String unit) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.unit = unit;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(itemId + "\t" + name + "\t" + price / 100 + "元\t" + stock + unit);
        if (createBy != null)
            builder.append("\t创建者:" + createBy);
        if (createTime != null)
            builder.append("\t创建时间:" + createTime);
        if (updateBy != null)
            builder.append("\t修改者:" + updateBy);
        if (updateTime != null)
            builder.append("\t修改时间:" + updateTime);
        return builder.toString();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
}
