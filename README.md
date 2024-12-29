学习记录

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
连接数据库
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

### 操作数据库语句

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
   ````

**可以对照sqlyog可视化记录查询语句**



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

### 创建表

````sql
CREATE TABLE [IF NOT EXISTS] `表名`(
	`字段名` 列类型 [属性] [索引] [注释],
	`字段名` 列类型 [属性] [索引] [注释],
    ...
	`字段名` 列类型 [属性] [索引] [注释]
)[表类型] [字符集设置] [注释]
````

````sql
CREATE TABLE IF NOT EXISTS `student`(
	`id` INT(4) NOT NULL AUTO_INCREMENT COMMENT `学号`,
    `name` VARCHAR(30) NOT NULL DEFAULT `匿名` COMMENT `姓名`,
    `password` VARCHAR(20) NOT NULL DEFAULT `123456` COMMENT `密码`,
    ...
)
````

