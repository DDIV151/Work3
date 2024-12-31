**写在前面：**

[OMS]: .\oms	"我是订单管理系统"
[WES]: .\wes	"我是天气查询系统"

这是，学习记录

# MySQL

## 初识MySQL

*数据库是所有软件体系中最核心的存在*

装了8.0.40,用解压版

---



### 什么是数据库

**概念**: 数据仓库,软件,安装在操作系统上

**作用**: 存储,管理数据



### 数据库分类

**关系型**

- MySQL,Oracle,Sql Server,DB2
- 表与表列与列间存储

**非关系型**

- redis,MongDB
- 对象存储

**DBMS(数据库管理系统)**

- 管理系统
- MYSQL:关系型数据库管理系统



### MySQL简介

~~详见百度百科~~

官网: www.mysql.com

常用版本: 5.7  8.0

### 常用命令

````cmd
# 连接数据库
mysql -uroot -p
````



````sql
-- 修改用户密码
update mysql.user set authentication_string=password('密码') where user='root' and Host = 'localhost';
-- 刷新权限
flush privileges;
-- 查看所有数据库
show databases;
-- 切换数据库
use 数据库名;
database changed;
-- 查看数据库中所有的表
show tables;
-- 显示数据库中所有表的信息
describe 数据库名;
-- 创建一个数据库
create database westos;
-- 退出
exit; 

-- 单行注释
/*
多行注释
*/
````



## 操作数据库



### *操作数据库语句*

1. 创建数据库

   ````sql
   CREATE DATABASE [IF NOT EXISIS] westos;
   ````

2. 删除数据库

   ````sql
   DROP DATABASE [IF EXISTS] westos;
   ````

3. 使用数据库

   ````sql
   -- tab 键上面的`可以使关键字变普通字符(虽然不变也能用)
   USE `user`;
   ````

4. 查看数据库

   ````sql
   SHOW DATABASE;-- 查看所有数据库
   SHOW CREATE DATABASE school;-- 查看创建数据库的语句
   ````

**记不住了可以去对照sqlyog可视化记录查询语句**

#### 如何创建数据库表

**格式：**

````sql
CREATE TABLE [IF NOT EXISTS] `表名`(
	`字段名` 列类型 [属性] [索引] [注释],
	`字段名` 列类型 [属性] [索引] [注释],
    ...
	`字段名` 列类型 [属性] [索引] [注释]
)[表类型] [字符集设置] [注释]
````

例：

````sql
CREATE TABLE IF NOT EXISTS `student`(
	`id` INT(4) NOT NULL AUTO_INCREMENT COMMENT `学号`,
    `name` VARCHAR(30) NOT NULL DEFAULT `匿名` COMMENT `姓名`,
    `password` VARCHAR(20) NOT NULL DEFAULT `123456` COMMENT `密码`,
    ...
)
````

注： **`** 是键盘上左上角的点，**并非单引号**



### *关于表的语句*

````sql
SHOW CREATE TABLE student;-- 查看student数据表的定义语句
DESC student;-- 显示student表的结构

-- 修改表名
ALTER TABLE teacher RENAME AS newteacher;
-- 增加表的字段
            表名        字段名  列属性
ALTER TABLE teacher ADD age    INT(11);
-- 修改表的字段(约束修改，重命名)
ALTER TABLE teacher1 MODIFY age VAECHAR(11);-- 约束
						  旧名字 新名字
ALTER TABLE teacher1 CHANGE age    newage INT(1);-- 重命名
-- 删除表的字段
ALTER TABLE teacher1 DROP age1;
-- 删除表（存在则删除）
DROP TABLE IF EXISTS teacher1;
````



**所有的创建和删除操作尽量加上判断，以免报错**

注意点：

- ``包裹字段名
- 注释用- 和/**/
- 关键字大小写不敏感
- 符号用英文



---



### 列的数据类型

1. 数值

   - tintint 1byte

   - smallint 2byte

   - mediumint 3byte

   - **int** 4byte

   - bigint 8byte

     

   - float 4byte
   - duble 8byte
   - decimal 字符串形式的浮点数(常用于金融计算)

2. 字符串
   - char 0 ~ 255
   - **varchar** 0 ~ 65535
   - tinytext 微型文本 2^8-1
   - **text** 文本串 2^16-1 保存大文本

3. 时间日期
   - date YYYY-MM-DD
   - time HH: mm: ss
   - **datetime** YYYY-MM-DD HH: mm: ss 最常用
   - **timestamp**  时间戳 1970.1.1 到现在的毫秒数

4. null
   - 未知, 没有值
   - 使用null进行运算, 结果为null
   - **不要用**

### **数据库的字段属性**

unsigned

- 无符号的整数

zerofill

- 0填充的
- 不足的位数用0填充 int(3) , 5->005

自增

- 通常为自增, 自动在上一条记录基础上+1(默认)

- 通常用来设计唯一的主键 index,必须是整形
- 可修改起始值和步长

非空

- 不赋值则报错
- 不选则默认为null

默认

- 设置默认值



---

### ***每个表必须存在的字段***

1. id 主键
2. `version` 乐观锁
3. is_delete 伪删除
4. gmt_create 创造时间
5. gmt_update 修改时间

---



### 数据表的类型

**关于数据库引擎：**

- INNODB （默认）
- MYISAM

|              | MYISAM | INNODB             |
| ------------ | ------ | ------------------ |
| 事务支持     | F      | T                  |
| 数据行锁定   | F      | T                  |
| 外键         | F      | T                  |
| 全文索引     | T      | F                  |
| 表空间的大小 | 较小   | 较大，约为前者两倍 |

常规使用操作：

- MYISAM 

  节约空间，速度快

- INODE 

  安全性高，事务的处理，多表多用户操作



**在物理空间存在的位置：**

所有的数据库文件都在data目录下，一个文件夹对应一个数据库

本质还是文件的存储！

MYSQL引擎在物理文件上的区别：

- INNODB在数据库表中只有一个*.frm以及上级目录下的ibdata1文件
- MYISAM对应文件
  - *.frm 表结构的定义文件
  - *.MYD 数据文件
  - *.MYI 索引文件（index）

**设置数据库表的字符集编码**

````sql
CHARSET=utf8
````

不设置就是mysql的默认字符集编码（可能是Latin1，不支持中文）

或者在my.ini设置

````ini
characte-set-server=utf8
````



## MySQL数据管理

### 外键

了解即可，阿里巴巴约定不用

---


### DML语言

数据库意义：数据存储，数据管理

DML语言：数据操作语言


- insert
- update
- delete

---

### 添加

**insert**

````sql
INSERT INTO 表名([字段1,字段2,字段3]) VALUES ('值1','值2','值3');-- 不写表的字段就挨个匹配，一般写也是一一对应地写

-- 插入多个字段：
... VALUES (),()...();
````

注意事项：

- 字段和字段之间用英文逗号隔开
- 字段可以省略，但是后面的值需要一一对应，不能少
- 可以同时插入多条数据，VALUES后面的值用逗号隔开即可

---




### 修改

**update**

````sql
UPDATE 表名 SET collnum_name=value1,[colnum_name=value1]... WHERE 条件；
-- 条件可选 但，不指定条件会改动所有表！！！
-- 可以修改多个值，用逗号隔开即可
````




  | 操作符           | 含义       | 范围            | 结果  |
  | ---------------- | ---------- | --------------- | ----- |
  | =                | 等于       | 5=6             | false |
  | <>或!=           | 不等于     | 5<>6            | true  |
  | >                | 大于       |                 |       |
  | <                | 等于       |                 |       |
  | >=               | 大于等于   |                 |       |
  | <=               | 小于等于   |                 |       |
  | BETWEEN...AND... | 在闭区间内 | BETWEEN 5 AND 6 | [5,6] |
  | AND              | &&         | 1>5 AND 1=1     | false |
  | OR               | \|\|       | 1>5 OR 1=1      | true  |


  例：

  ````sql
  UPDATE `student` SET `name`='SSSS.',`gender`= 'male' WHERE id = 1;
  ````

  注意：

  - colnum_name是数据库的列，尽量带上``
  - 筛选条件不指定会修改所有的列
  - value可以是一个具体值，也可以是一个变量
  - 多个属性间用逗号隔开

---



### 删除

- delete

  ````sql
  -- 语法：
  DELETE FROM 表名 条件；
  -- 不加条件就是全删
  ````

- TRUNCATE

  完全清空一个数据库表，表的结构和索引约束不变

  ````sql
  TRUNCATE 表名；
  ````

二者相同点：都能删除数据且不会删除表结构

不同点：TRUNCATE重新设置自增列，计数器归零，且不会影响事务



## DQL查询数据

### DQL

 (Data Query LANGUAGE: 数据查询语言 )

- 所有的查询操作（不论简单复杂）都用它
- ***数据库中最核心的语言，最重要的语句***
- 使用频率最高的语言 



### 查询指定字段

````sql
-- 查询全部的学生 SELECT 字段 FROM 表
SELECT * FROM STUDENT
-- 查询指定字段
SELECT `StudentNo`,`StudentName` FROM student
-- 别名 AS... 可以给字段起，也可以给表起
SELECT `StudentNo` AS 学号,`StudentName` AS 学生姓名 FROM student AS s
-- 函数 CONCAT(a,b)
SELECT CONCAT('姓名：',STudentName) AS 新名字 FROM student
````

有时列名字不是见名知意，可以起别名



**去重** (DISTINCT) ：

作用：去除SELECT查询结果中的重复数据，只显示一条

````sql
SELECT * FROM result -- 查询全部考试成绩
SELECT `StudentNo` FROM result -- 查询哪些同学参加了考试
SELECT DISTINCT `StudentNo` FROM result -- 发现重复数据，去重
````



### where条件字句

作用：检索数据中符合条件的值

**逻辑运算符**

| 运算符  | 语法    | 描述   |
| ------- | ------- | ------ |
| and &&  | a and b | 逻辑与 |
| or \|\| | a or b  | 逻辑或 |
| not !   | not a   | 逻辑非 |

尽量使用英文字母

**比较运算符**（模糊查询）

| 运算符      | 语法               | 描述                    |
| ----------- | ------------------ | ----------------------- |
| IS NULL     | a is null          |                         |
| IS NOT NULL | a is not null      |                         |
| BETWEEN     | a between b and c  |                         |
| **LIKE**    | a like b           | SQL匹配，a匹配到b则为真 |
| **IN**      | a in (a1,a2,a3...) | a在a1，a2...中则为真    |

````sql
-- like结合%(0到任意个字符) _(一个字符)
SELECT `StudentNo`,`StudentName` FROM `student` WHERE StudentName LIKE '刘_'-- 查询姓刘且名只有一个字的同学
````



### 联表查询

 ````sql
 -- join 连接的表 on 连接查询
 -- where 等值查询
 
 -- 查询缺考的同学
 SELECT s.studentNO,studentName,SubjectNo,StudentResult
 FROM student s
 LEFT JOIN result r -- 在学生列表却不在考试结果列表
 ON s.studentNO = r.studentNO
 WHERE StudentResult IS NULL -- 所以缺考同学结果为空
 ````

| 操作       | 描述                                       |
| ---------- | ------------------------------------------ |
| inner join | 表中有一个匹配就返回值                     |
| left join  | 会从左表中返回所有的值，即使右表中没有匹配 |
| right join | 会从右表中返回所有的值，即使左表中没有匹配 |

### 分页和排序

### 子查询和嵌套查询

### 分组和过滤

````sql
SELECT　Subjectname,AVG(studentresult) as 平均分,MAX(studentresult) as 最高分,MIN(studentresult) 最低分
FROM result r
INNER JOIN　`subject` sub
ON r.`subjectNO` = sub.`subjectNO`
Group BY r.Subjectno -- 通过什么字段来分组
HAVING 平均分>80
````

___

## MySQL函数

### 常用函数

### 聚合函数

| 函数    | 描述   |
| ------- | ------ |
| COUNT() | 技术   |
| SUM()   | 求和   |
| AVG()   | 平均值 |
| MAX()   | 最大值 |
| MIN()   | 最小值 |

````sql
SELECT COUNT(studentname) FROM student -- COUNT(指定列)，忽略所有null
SELECT COUNT(*) FROM student -- COUNT(*),不会忽略null,本质计算行数
SELECT COUNT(*) FROM student -- COUNT(1),同上
````

### 数据库级别的MD5加密

MD5 主要增强算法复杂度和不可逆性

MD5不可逆，具体的值的MD5是一样的

MD5破解网站的原理的背后有一个字典，记录MD5加密后的值与加密的前值

````sql
-- 测试md5加密
CREATE TABLE `testmd5`(
	`id` INT(4) NOT NULL,
    `name` VARCHAR(20) NOT NULL,
    `pwd` VARCHAR(50) NOT NULL,
    PRIMARY KEY(`id`)
)ENGINE=INNODB DEFAULT CHARSET=utf8
-- 明文密码
INSERT INTO testmd5 VALUES(1,'zhangsan','123456'),(2,'lisi','123456'),(3,'wangwu','123456')
-- 加密
UPDATE testmd5 SET pwd=MD5(pwd)
````

---



## 事务

### 事务的ACID原则

*要么都成功，要么都失败*

下面有个例子：
1. sql执行 a转账给b200：a 1000-->800 b 200
2. sql执行 b收到a转账200：a800 b 200 -->b 400

如果执行一半就麻烦了
**所以将一组sql放在一个批次执行**



事务原则：**ACID原则**（原子性，一致性，隔离性，持久性）

**原子性**：要么都成功，要么都失败（如上）

**一致性**：针对一个事物，操作前后的状态一致（例子中a，b财产总和为1200）

**持久性**：事物结束后的数据不随着外界原因倒是数据丢失（**事务一旦提交就不可逆**，事务没有提交则恢复原状，提交了就持久化到数据库）

**隔离性**：多个用户并发访问数据库时，数据库为每个用户开启的事务不能被其它事物的数据操作所干扰

（以下是事务的隔离级别）
- 脏读：一个数据读取了另一个事务没提交的数据
- 不可重复读(避免在一个事务中前后读取到不同数据)
- 幻读：指读取到了别的事务新插入的数据

---

### 执行事务

关于事务自动提交：

````sql
SET autocommit = 0 -- 关闭
SET autocommit = 1 -- 默认开启
````

手动处理事务：

````sql
SET autocommit = 0 -- 关闭自动提交
START TRANSACTION -- 标记一个事务的开始，从这之后的sql都在一个事务内
INSERT xx -- 语句
...
-- 提交：持久化（成功）
COMMIT 
-- 回滚：回到原来的样子（失败）
ROLLBACK
-- 事务结束
SET autocommit = 1 -- 开启自动提交

-- 事务特别长，中间可以设置事务保存点：
SAVEPOINT 保存点名 -- 设置一个事物保存点
ROLLBACK TO SAVEPOINT 保存点名 -- 回滚到一个保存点
RELEASE SAVEPOINT 保存点 -- 撤销保存点
-- 一个事务可以用多个保存点
````

## 索引

### 索引的分类

*在一个表中，主键索引只能有一个，唯一索引可以有很多个*

- 主键索引 (PRIMARY KEY)
  - 唯一的标识，主键不可重复，只能用一个列作为主键
- 唯一索引 (UNIQUE KEY)
  - 避免重复的列出现，唯一索引可以重复，多个列都可以标识位唯一索引
- 常规索引 (KEY/INDEX)
  - 默认的，index或key关键字来设置
- 全文索引 (FULLTEXT)
  - 在特定的数据库引擎下才有，比如MyISAM
  - 快速定位数据

````sql
-- 索引的使用
-- 1.在创建表的时候给字段增加所以
-- 2.创建完毕后，增加索引

-- 显示所有的索引信息：
SHOW INDEX FROM student

-- 增加一个索引(索引名)
ALTER TABLE school.student ADD FULLTEXT `studentName`;

-- EXPLAIN 分析sql执行的情况
EXPLAIN SELECT * FROM student;
````

### 测试索引

````sql
略
````

~~暂时不测了（手动滑稽）~~

小数据量的时候，用户不大，大数据作用才明显

### 索引原则

- 索引不是越多越好
- 不要对进程变动数据加索引
- 小数据量的表不需要加索引
- 索引一般加在常用来查询的字段上

**关于索引的数据结构**：

Hash类型的索引：

Btree：INNODB的默认数据结构

---

## 权限管理和备份

### 用户管理

1. SQLyog可视化，在页面顶端

2. SQL命令

   用户表：mysql.user

   本质：对用户表进行增删改查

   ````sql
   -- 创建用户
   CREATE USER ddiv151 IDENTIFIED BY 'password'
   
   -- 删除用户
   DROP 用户名
   
   -- 修改当前用户密码
   SET PASSWORD = PASSWORD('newpassword')
   
   -- 修改指定用户密码
   SET PASSWORD FOR 用户名 = PASSWOED('newpassword')
   
   -- 重命名
   RENAME USER 用户名 TO 新用户名
   
   -- 用户授权
   -- ALL PRIVILEGES: 除了给别人授权以外的几乎全部权限
   GRANT ALL PRIVILEGES ON 库.表 TO 用户名 -- *.*-->全部库和表
   
   -- 撤销权限
   REVOKE 权限 ON 库.表 FROM 用户名 
   
   -- 查看权限
   SHOW GRANTS FOR 用户名 -- 查看看指定用户的权限
   SHOW GRANTS FOR root@localhost -- 查看root权限（？
   ````

   ~~记不得了测一下~~

### 备份

备份的作用：

- 保护重要数据
- 数据转移

MYSQL数据库**备份**的方式:

1. 直接拷贝(data目录)

2. SQLyog右键有

3. 命令行：

   ````cmd
   # mysqldump -h 主机 -u 用户名 -p密码 数据库 表名 >导出位置
   mysqldump -h localhost -root -p123456 school student >D:/students.sql
   # 可以导出多张表，在表名后面写其它的，用空格隔开
   ````

**导入**数据：

这里只写命令行方式

````cmd
#先登录账户，然后使用source命令
#如果要导入表，就切换到某个数据库
source D:/students.sql
#或者不登陆，见导入方法
````

---



##  规范数据库设计

### 为什么需要设计

因为很多数据库比较复杂

**糟糕的数据库设计：**

- 数据冗余，浪费空间
- 数据插入和删除会很麻烦，有异常（物理外键导致的）
- 程序的性能差

**良好的数据库设计：**

- 节省内存空间
- 保证数据库的完整性
- 方便开发系统

**软件开发中，关于数据库的设计：**

- 分析需求：分析业务和需要处理的用户需求
- 概要设计：设计关系图和E-R图



### 设计数据库的步骤

用博客网站举例

- 收集信息，分析需求
  - 用户表（用户登录注销，用户的个人信息，写博客，创建分类）
  - 分类表（文章分类，谁创建的）
  - 文章表（文章的信息）
  - 友链表（友链信息）
  - 自定义表（系统信息，某个关键的字，或者一些关键字段）key：value
- 表示实体（把需求落地到每个字段）
  - 写博客：user-->blog
  - 创建分类：user-->category
  - 关注：user-->user
  - 友链：links
  - 评论：user-user-blog



### 三大范式

为啥需要数据规范化？

- 信息重复
- 更新异常
- 插入异常
  - 无法正常显示信息
- 删除异常

**第一范式**

原子性：保证每一列不可再分

**第二范式**

前提：满足第一范式
每张表只描述一件事情

**第三范式**

前提：满足第一范式与第二范式
确保数据表中的每一列数据都和主键直接相关，而不是间接相关

**规范和性能的问题**

关联查询的表不得超过三张表

- 考虑商业化的需求和提升用户的体验，数据库的性能更加重要
- 在规范性能的问题时，需要适当考虑规范性
- 故意给某些表增加一些冗余的字段（从多表查询变为单表查询）
  订单号-商品信息
- 故意增加一些计算列（从大数据量降低为小数据量的查询：索引）

# JDBC

## 数据库驱动

程序通过数据库驱动和数据库连接

## JDBC

为了简化开发人员对数据库的统一操作，提供了一个Java操作数据库的规范，俗称JDBC

规范由具体的厂商去做

对于开发人员来说，学JDBC就好了

开发人员->JDBC->数据库驱动->数据库



## 第一个JDBC程序

````java
//1.加载驱动
Class.forName("com.mysql.cj.jdbc.Driver");//适用于8.0以上版本，不一定要加载

//2.用户信息和url
String url = "jdbc:mysql://localhost:3306/oms?useUnicode=true&characterEncoding=utf8&useSSL=false";
//上面的url连接了本地的oms数据库
String user = "root";
String password = "123456";

//3.连接数据库，Connection代表数据库
Connection con = DriverManager.getConnection(url, user, password);

//4.执行sql的对象Statment
Statement stmt = con.createStatement();
//接下来利用Statment对象执行下面的sql
String sql = "select * from users";

//5.ResultSet对象接收返回值，rs中封装了所有结果
ResultSet rs = stmt.executeQuery(sql);
while (rs.next()) {
	System.out.println("==========");
	System.out.println("id="+rs.getInt("id"));
	System.out.println("name="+rs.getString("name"));
	System.out.println("email="+rs.getString("email"));
	System.out.println("password="+rs.getString("password"));
	System.out.println("birthday="+rs.getDate("birthday"));
}

//6.最后释放资源
re.close();
stmt.close();
con.close();
````

**DriverManager**

````java
//DriverMannager.registerDriver(new com.mysql.cj.jdbc.Driver.Driver());
Class.forName("com.mysql.cj.jdbc.Driver");
Connection con = DriverManager.getConnection(url, user, password);

//con代表数据库
//数据库设置自动提交
//事务提交
//事务滚回
connection.rollback();
connection.commit();
connection.setAutoCommit;
````

**URL**

````java
String url = "jdbc:mysql://localhost:3306/oms?useUnicode=true&characterEncoding=utf8&useSSL=false";

//mysql -- 3306
//协议://主机地址//端口号/数据库名?参数1&参数2&参数3

//oralce -- 1521
//jdbc:oracle:thin:@localhost:1521:sid
````

**Statement**

执行sql的对象

````java
statement.executeQuery();//查询操作，返回ReaultSet
statement.execute();//执行任何SQL
statement.executeUpdate();//更新、插入、删除都用这个，返回受影响的行数
````

**ResultSet**

返回结果集，封装了所有的查询结果

````java
resultSet.getObject();//不知道类型
//知道结果时
resultSet.getString();
resultSet.getInt();
resultSet.getFloat();
resultSet.getDate();
````

遍历，指针

````java
resultSet.beforeFirst();//移动到最前
resultSet.afterLast();//移动到最后
resultSet.next();.//移动到下一行
resultSet.previous();//移动到前一行
resultSet.absolute(row);//移动到指定行
````

**释放资源**

````java
resultSet.close();
statement.close();
connection.close();//很耗资源的
````

##  statement对象

