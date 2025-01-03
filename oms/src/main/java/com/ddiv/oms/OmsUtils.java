package com.ddiv.oms;

import com.ddiv.jdbc.JDBCUtils;
import com.ddiv.jdbc.Transaction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class OmsUtils extends JDBCUtils {

    //添加新商品
    public static void createItem(String name, int price, int stock, Object... details) throws SQLException {
        //
        StringBuilder sql = new StringBuilder("insert into oms_item(name,price,stock) values(?,?,?)");
        int num = details.length;
        //保存每个值的数组
        int len = 3 + num / 2;
        Object[] params = new Object[len];
        params[0] = name;
        params[1] = price;
        params[2] = stock;
        //第一种情况：添加的商品没有非主要字段（创建人，单位等）
        if (num == 0)
            try {
                //直接尝试执行
                executeUpdate(sql.toString(), params);
                return;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("插入失败");
            }
            //第二种情况：错误，传入了不符合表中允许自定义的字段数量
        else if (num % 2 != 0)
            throw new RuntimeException("添加商品失败 请确认额外字段合法性");

        //然后是自定义字段没错的第三种情况
        //需要按照"字段名1,值1,字段名2,值2的格式填入"
        try {
            //1.用StringBuilder修改默认sql
            //1.1 保存字段名的字符串
            StringBuilder field = new StringBuilder();
            for (int i = 0; i < num; i += 2) {
                field.append(",").append(details[i]);
            }

            //1.2 传入的sql中需要添加的，用来占位的问号字符串
            StringBuilder detail = new StringBuilder();

            //1.3 更新保存每个值的数组
            for (int i = 1, cut = 3; i < num; i += 2, cut++) {
                detail.append(",").append("?");
                params[cut] = details[i];
            }
            //1.4 修改sql
            sql.insert(37, field.toString());
            sql.insert(sql.length() - 1, detail.toString());

            //2 尝试执行
            executeUpdate(sql.toString(), params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("添加失败");
        }
    }

    /**
     * 修改商品
     *
     * @param itemId  商品id
     * @param details 要修改的信息，格式：字段1,值1,字段2,值2......
     * @throws SQLException 修改失败异常
     */
    public static int updateItem(int itemId, Object... details) throws SQLException {
        StringBuilder sql = new StringBuilder("update `oms_item` set ");
        Object[] params = new Object[details.length / 2];
        int num = details.length;
        int result = -1;
        for (int i = 0, cut = 0; i < num; i += 2, cut++) {
            sql.append("`" + details[i] + "`" + "=?,");
            params[cut] = details[i + 1];
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where `item_id`=" + itemId);
        result = executeUpdate(sql.toString(), params);
        return result;
    }

    //查找商品(根据商品名
    public static ArrayList<Item> queryItem(String name) throws SQLException {
        ArrayList<Item> items = new ArrayList<>();
        String sql = "select item_id,name,price,stock,unit from oms_item where name like ? and is_delete=0";
        items = executeQuery(Item.class, sql, name);
        return items;
    }

    //修改商品数量
    public static void updateItemNum(int itemId, int num) throws SQLException {
        Transaction transaction = JDBCUtils.transactionStart();
        if (itemId <= 0 || num < 0)
            throw new RuntimeException("参数错误");
        transaction.setPreparedStatement("update oms_item set stock=" + num + " where item_id=" + itemId + " and is_delete=0");
        transaction.execute();
        transaction.commit();
    }

    //删除商品
    public static void deleteItem(int itemId) throws SQLException {
        Transaction transaction = JDBCUtils.transactionStart();
        transaction.setPreparedStatement("select count(*) from oms_order_detail where item_id=" + itemId);
        transaction.execute();
        ResultSet resultSet = transaction.getPreparedStatement().getResultSet();
        resultSet.next();
        if (resultSet.getInt(1) > 0) {
            throw new RuntimeException("正在尝试删除订单中存在的商品？");
        }
        transaction.setPreparedStatement("update oms_item set is_delete = 1 where item_id=" + itemId);
        transaction.execute();
        transaction.commit();
    }

    //添加订单
    public static void addOrder(Order order) throws Exception {
        //存储order_detail物品信息的列表
        ArrayList<OrderDetail> details = order.getItems();

        //储存商品信息的列表
        ArrayList<Item> items = new ArrayList<>();


        //开启事务
        Transaction tr = JDBCUtils.transactionStart();
        try {
            int amount = 0;
            //首先验证订单合法性，这里选择验证库存和数量
            tr.setPreparedStatement("select item_id,price,stock from oms_item");
            ResultSet itemQuery = tr.execute();
            items = JDBCUtils.exchangeData(Item.class, itemQuery);
            int cut = 0;
            for (Item item : items) {
                for (OrderDetail orderDetail : details) {
                    if (item.getItemId() == orderDetail.getItemId()) {
                        if (orderDetail.getItemNum() > item.getStock() || orderDetail.getPrice() != item.getPrice())
                            throw new RuntimeException("数据不合法，订单创建失败");
                        //顺便统计一下订单金额
                        amount += orderDetail.getItemNum() * orderDetail.getPrice();
                        cut++;
                        break;
                    }
                }
            }
            //订单中有不存在的商品
            if (cut < details.size())
                throw new RuntimeException("数据不合法，订单创建失败");

            //获取下一个（本次使用）订单id
            tr.setPreparedStatement("select max(order_id) from oms_order");
            ResultSet rs = tr.getPreparedStatement().executeQuery();
            rs.next();
            int id = rs.getInt(1) + 1;

            //准备插入oms_detail
            tr.setPreparedStatement("insert into oms_order_detail(`order_id`,`item_id`,`item_num`,`price`) values(?,?,?,?)");
            for (OrderDetail orderDetail : details) {
                //设置刚刚获取的预留id
                orderDetail.setOrderId(id);
                tr.execute(orderDetail.getOrderId(), orderDetail.getItemId(), orderDetail.getItemNum(), orderDetail.getPrice());

            }
            if (amount <= 0)
                throw new RuntimeException("商品金额不合法");
            //到这里插入detail完成,准备插入oms_order
            order.setOrderAmount(amount);
            tr.setPreparedStatement("insert into oms_order(order_amount) values(" + amount + ")");
            tr.execute();
            //到这里都成功就提交
            tr.commit();
        } catch (Exception e) {
            tr.rollback();
            throw e;
        } finally {
            tr.close();
        }
    }

    //查看订单
    public static void viewOrder() throws Exception {
        Transaction transaction = JDBCUtils.transactionStart();
        transaction.setPreparedStatement("select oms_order_detail.order_id,oms_order_detail.item_id,price,item_num from oms_order inner join oms_order_detail on oms_order.order_id=oms_order_detail.order_id");
        transaction.execute();
        ResultSet resultSet = transaction.getPreparedStatement().getResultSet();
        if (resultSet == null)
            System.out.println("当前无订单");
        ArrayList<OrderDetail> details = JDBCUtils.exchangeData(OrderDetail.class, resultSet);
        transaction.setPreparedStatement("select oms_order.order_id,order_amount,unit,create_time from oms_order where is_delete = 0");
        transaction.execute();
        resultSet = transaction.getPreparedStatement().getResultSet();
        ArrayList<Order> orders = JDBCUtils.exchangeData(Order.class, resultSet);
        for (Order order : orders) {
            for (OrderDetail orderDetail : details) {
                if (order.getOrderId() == orderDetail.getOrderId()) {
                    order.getItems().add(orderDetail);
                }
            }
        }
        transaction.setPreparedStatement("select name,item_id,price,unit from oms_item where is_delete = 0");
        transaction.execute();
        resultSet = transaction.getPreparedStatement().getResultSet();
        ArrayList<Item> items = JDBCUtils.exchangeData(Item.class, resultSet);
        //排序：
        Collections.sort(orders);
        for (Order order : orders) {
            int cut = 0;
            System.out.println("======");
            System.out.println(order);
            for1:
            for (Item item : items) {
                for (OrderDetail orderDetail : order.getItems()) {
                    if (orderDetail.getItemId() == item.getItemId()) {
                        System.out.println("商品id："+item.getItemId()+"商品名："+item.getName()+"\n商品单价："+item.getPrice()+item.getUnit());
                        System.out.println("数量：" + orderDetail.getItemNum());
                        cut++;
                        if (cut == orderDetail.getItemNum())
                            break for1;
                    }
                }
            }
        }
        transaction.commit();
    }

}
