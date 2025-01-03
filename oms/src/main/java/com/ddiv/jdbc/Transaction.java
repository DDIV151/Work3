package com.ddiv.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transaction {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    public Transaction() {
    }

    public Transaction(Connection connection) {
        this.connection = connection;
    }

    public Transaction(Connection connection, String sql) {
        this.connection = connection;
        setPreparedStatement(sql);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    public void setPreparedStatement(String sql) {
        try {
            this.preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("事务sql设置失败");
        }
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void close() throws SQLException {
        JDBCUtils.close(connection, preparedStatement, resultSet);
    }

    public void commit() throws SQLException {
        connection.commit();
    }

    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * 设置好sql后, 方法按位插入实参并执行
     *
     * @param objs 实参列表
     * @throws SQLException 执行失败
     */
    public ResultSet execute(Object... objs) throws SQLException {
        fillSql(objs);
        preparedStatement.execute();
        return resultSet = preparedStatement.getResultSet();
    }

    private void fillSql(Object... objs) throws SQLException {
        for (int i = 0; i < objs.length; i++) {
            preparedStatement.setObject(i + 1, objs[i]);
        }
    }
}
