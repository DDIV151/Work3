package com.ddiv.jdbc;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtilsTest extends TestCase {

    public void testGetConnection() throws SQLException {
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        if (stmt != null) {
            //进行简单的增删改查测试
            stmt.executeUpdate("drop table if exists test");
            stmt.executeUpdate("create table if not exists test (id int primary key, name varchar(20))");
            stmt.executeUpdate("insert into test values(1,'test')");
            ResultSet rs = stmt.executeQuery("select * from test");
            if (rs.next()) {
                if (!rs.getString("name").equals("test") || rs.getInt("id") != 1) {
                    throw new SQLException();
                }
            } else
                throw new SQLException();
        }
    }

    public void testClose() throws SQLException {
        //测试为空的情况
        JDBCUtils.close(null, null, null);
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from test");
        JDBCUtils.close(con, null, null);
        JDBCUtils.close(con, stmt, null);
        JDBCUtils.close(con, stmt, rs);
    }
}