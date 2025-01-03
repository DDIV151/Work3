package com.ddiv.oms;

import java.util.ArrayList;
import java.util.Date;

public class Order implements Comparable<Order> {
    @Override
    public int compareTo(Order o) {
        if (this.orderAmount != o.orderAmount)
            return orderAmount - o.orderAmount;
        else
            return this.getCreateTime().compareTo(o.getCreateTime());
    }

    private int orderId;

    private int orderAmount = 0;

    private Date createTime;

    private String unit = "分";

    private ArrayList<OrderDetail> items = new ArrayList<>();

    public Order() {
    }

    public Order(ArrayList<OrderDetail> items) {
        this.items = items;
        for (OrderDetail item : items) {
            orderAmount += item.getPrice() * item.getItemNum();
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderAmount() {
        return orderAmount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public void setOrderAmount(int orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ArrayList<OrderDetail> getItems() {
        return items;
    }

    public void setItems(ArrayList<OrderDetail> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Order Id=" + orderId + ", 总金额=" + orderAmount + unit + ", 创建时间=" + createTime;
    }
}
