package com.ddiv.jdbc;


import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class JDBCUtils {
    private static String driver = null;
    private static String username = null;
    private static String password = null;
    private static String url = null;

    public static String getDriver() {
        return driver;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static String getUrl() {
        return url;
    }

    //获取db.properties下的配置文件
    static {
        try {
            InputStream in = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");
            Properties prop = new Properties();
            prop.load(in);
            driver = prop.getProperty("driver");
            username = prop.getProperty("username");
            password = prop.getProperty("password");
            url = prop.getProperty("url");
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    //释放资源
    public static void close(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * 查询结果 转换为对应实体对象列表
     *
     * @param <T>     实体类型
     * @param clazz   实体字节码文件对象
     * @param sql     仅含形参的查询语句
     * @param details 用户传入sql对应部分的实参列表
     */
    public static <T> ArrayList<T> executeQuery(Class<T> clazz, String sql, Object... details) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<T> results = new ArrayList<>();
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < details.length; i++) {
                ps.setObject(i + 1, details[i]);
            }
            rs = ps.executeQuery();
            results = exchangeData(clazz, rs);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询失败");
        } finally {
            close(conn, ps, rs);
        }
        return results;
    }

    /**
     * 修改 返回变动的行数
     *
     * @param sql     仅含形参的查询语句
     * @param details 用户传入sql对应部分的实参列表
     * @return int 受影响行数
     */
    public static int executeUpdate(String sql, Object... details) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        int num = 0;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < details.length; i++) {
                ps.setObject(i + 1, details[i]);
            }
            num = ps.executeUpdate();
            return num;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("操作失败");
        } finally {
            close(conn, ps, null);//释放资源
        }
    }

    /**
     * 转换数据 JDBC结果集转实体
     *
     * @param resultSet 结果集
     * @param clazz     实体字节码文件对象
     * @param <T>       实体类型
     * @throws Exception sql异常 NoClass异常等等
     */
    public static <T> ArrayList<T> exchangeData(Class<T> clazz, ResultSet resultSet) throws Exception {
        //获取全部类方法  包括父类的
        Method[] declaredMethods = clazz.getMethods();
        ArrayList<T> list = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        while (resultSet.next()) {
            //反射实例化对象
            T obj = clazz.newInstance();
            //遍历类调用方法
            for (Method method : declaredMethods) {
                //获取方法名
                String name = method.getName();
                if (!name.startsWith("set")) {
                    //只要setter
                    continue;
                }
                //获取数据库名 驼峰命名法转数据库字段命名法
                String dbName = getDbName(name);
                //遍历数据库所有列
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    //抓取指定列赋值
                    if (metaData.getColumnName(i).equals(dbName)) {
                        if (resultSet.getObject(i) != null) {
                            //赋值
                            setValue(obj, method, resultSet, i);
                        }
                        break;
                    }
                }
            }
            list.add(obj);
        }
        return list;
    }

    /**
     * 赋值操作，
     * 主要是处理数据类型
     * 此处只简单处理下基本数据类型和Date类型
     *
     * @param obj       泛型对象
     * @param method    方法
     * @param resultSet 结果集
     * @param i         脚标
     * @param <T>       泛型
     */
    private static <T> void setValue(T obj, Method method, ResultSet resultSet, int i) throws SQLException, InvocationTargetException, IllegalAccessException, InvocationTargetException {
        //Setter方法只有一个参数，获取参数类型名称
        String name = method.getParameterTypes()[0].getName().toLowerCase();
        if (name.contains("string")) {
            method.invoke(obj, resultSet.getString(i));
        } else if (name.contains("short")) {
            method.invoke(obj, resultSet.getShort(i));
        } else if (name.contains("int") || name.contains("integer")) {
            method.invoke(obj, resultSet.getInt(i));
        } else if (name.contains("long")) {
            method.invoke(obj, resultSet.getLong(i));
        } else if (name.contains("float")) {
            method.invoke(obj, resultSet.getFloat(i));
        } else if (name.contains("double")) {
            method.invoke(obj, resultSet.getDouble(i));
        } else if (name.contains("boolean")) {
            method.invoke(obj, resultSet.getBoolean(i));
        } else if (name.contains("date")) {
            method.invoke(obj, resultSet.getDate(i));
        } else {
            method.invoke(obj, resultSet.getObject(i));
        }

    }

    /**
     * 实体setter名称转对应数据库列的列名
     * 需要遵守命名规范，java（驼峰命名法），数据库（全小写，单词间用'_'隔开）
     *
     * @param name setter名称
     * @return 数据库列名
     */
    private static String getDbName(String name) {
        //根据setter命名规则获取对应的属性名
        name = name.substring(3, 4).toLowerCase() + name.substring(4);
        //获取数据库对应列名
        StringBuffer buffer = new StringBuffer();
        char[] nameChars = name.toCharArray();
        for (char nameChar : nameChars) {
            if (nameChar >= 'A' && nameChar <= 'Z') {
                //将大写字母转换为下划线和对应的小写字母组合
                buffer.append("_").append(String.valueOf(nameChar).toLowerCase());
            } else {
                buffer.append(String.valueOf(nameChar));
            }
        }
        return buffer.toString();
    }

    /**开启事务
     * @return Transaction 对象
     */
    public static Transaction transactionStart() {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            return new Transaction(conn);
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("事务失败");
        }

    }

}
