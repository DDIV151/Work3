package com.ddiv.oms;

import com.ddiv.jdbc.JDBCUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import static com.ddiv.oms.OmsUtils.*;

public class OmsUtilsTest {
    //创建商品
    @Test
    public void testCreateItem() throws SQLException {
        createItem("test", 1, 1, "update_by", "111", "create_by", "ddiv151");
        createItem("test", 1, 2);

    }

    @Test
    public void testQueryItem() throws Exception {
        /*
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("select name,price,stock,unit,item_id,create_by,create_time from oms_item where is_delete = 0");
        preparedStatement.executeQuery();
        ResultSet resultSet = preparedStatement.getResultSet();
        ArrayList<Item> results = JDBCUtils.exchangeData(Item.class,resultSet);
        */
        ArrayList<Item> results = queryItem("test");
        if (results.size() == 0) {
            System.out.println("没找到商品");
        }
        for (Item item : results) {
            System.out.println(item);
        }
    }

    //更新商品信息
    @Test
    public void testUpdateItem() throws SQLException {
        if (updateItem(3, "price", 200, "name", "bear") == -1)
            throw new RuntimeException("修改失败");
    }

    //删除商品
    @Test
    public void testDeleteItem() throws SQLException {
        if (updateItem(2, "is_delete", 1) == -1)
            throw new RuntimeException("删除失败");
    }

    //生成新订单
    @Test
    public void testAddOrder() throws Exception {
        ArrayList<OrderDetail> details = new ArrayList<>();
        details.add(new OrderDetail(1, 3, 200));
        Order order = new Order(details);
        addOrder(order);

        //故意插入一个错误数据
        details.add(new OrderDetail(2, 3, 100));
        order = new Order(details);
        try {
            addOrder(order);
        } catch (Exception e) {
            if (!e.getMessage().equals("数据不合法，订单创建失败")) {
                throw new RuntimeException("数据监测失败");
            }
        }

    }

    @Before
    public void remakeSql() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        PreparedStatement p = connection.prepareStatement("insert into oms_item(name,price,stock,unit,create_by) values ('test',200,500,'吨','ddiv151')");
        p.execute();
    }

    @Test
    public void testUpdateItemNum() throws SQLException {
        updateItemNum(1, 20);
    }

    @Test
    public void testTestDeleteItem() throws SQLException {
        deleteItem(2);
        try {
            deleteItem(1);
        } catch (Exception e) {
            if (!e.getMessage().equals("正在尝试删除订单中存在的商品？"))
                throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    public void testViewOrder() throws Exception {
        viewOrder();
    }
}