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
        //进行简单的增删改查测试
        try {
            stmt.executeUpdate("drop table if exists test_java_get_connection");
            stmt.executeUpdate("create table if not exists test_java_get_connection (id int primary key, name varchar(20))");
            stmt.executeUpdate("insert into test_java_get_connection values(1,'test')");
            ResultSet rs = stmt.executeQuery("select * from test_java_get_connection");
            if (rs.next()) {
                if (!rs.getString("name").equals("test") || rs.getInt("id") != 1) {
                    throw new SQLException();
                }
            } else
                throw new SQLException();
        } finally {
            stmt.executeUpdate("drop table if exists test_java_get_connection");
            stmt.close();
            con.close();
        }

    }

    public void testClose() throws SQLException {
        //测试为空的情况
        JDBCUtils.close(null, null, null);
        Connection con = JDBCUtils.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("show tables");
        JDBCUtils.close(con, null, null);
        JDBCUtils.close(con, stmt, null);
        JDBCUtils.close(con, stmt, rs);
    }
}