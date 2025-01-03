# 简单订单管理系统

## JDBC工具类部分

* 处理数据库连接

  用类加载器读取db.properties 进行连接，编辑时比起SQLyog我更用的是idea

* 执行增删查改操作 

  利用上次没学的泛型知识结合反射写了转实体类的方法，结合它们增删改查

* 解决**SQL注入问题** 

  用preparedStatement

* 添加**事务管理**

   加了

* 包含异常处理和资源释放

  试着做了





## 具体实现部分

* 在创建订单时，实施数据验证，确保订单信息的完整性和准确性。例如，检查商品是否存在，价格是否合法等等。

  订单没设置是否完成标志，但姑且算了库存

* 如果想要删除已经存在在订单中的商品，你要怎么处理？

  throw，搞清楚再删

* 避免使用SELECT *

  没用哦

* 排序我用转化成实体类排的

### 数据库设计

至少需要记录以下信息:

* 商品: 商品编号、商品名、商品价格

  oms_item

* 订单: 订单编号、**商品信息**(考虑如何合理存储关联信息)、下单时间、订单价格

  用了一个中间表oms_order_detail记录订单详情(含几份哪种商品)

  订单其它信息储存在oms_order

  要商品信息时我选择读商品表

这里贴上我用的数据库

````sql
create table oms_item
(
    item_id     int unsigned auto_increment comment 'id'
        primary key,
    name        varchar(20)                                not null comment '商品名称',
    price       int unsigned                               not null comment '商品价格，单位为RMB分',
    stock       int unsigned                               not null comment '商品库存数量',
    unit        varchar(10)      default '个'              not null comment '单位',
    is_delete   tinyint unsigned default '0'               not null comment '逻辑删除标志0有效，1无效',
    create_by   varchar(20)      default 'unknown_user'    null,
    create_time datetime         default CURRENT_TIMESTAMP not null,
    update_by   varchar(20)      default 'unknown_user'    null,
    update_time datetime         default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
)
    comment '商品信息';

create table oms_order
(
    order_id     int unsigned auto_increment comment '订单编号'
        primary key,
    order_amount int unsigned     default '0'               not null comment '订单金额，0表示未计算（无效）',
    unit         varchar(10)      default '分'              not null comment '单位（RMB）',
    create_by    varchar(20)      default 'unknown_user'    null comment '创建人',
    create_time  datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime         default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    update_by    varchar(20)      default 'unknown_user'    null comment '修改人',
    is_delete    tinyint unsigned default '0'               not null comment '0表示有效删除，1无效'
)
    comment '订单管理系统';

create table oms_order_detail
(
    order_id    int unsigned                               not null comment '对应oms_order的order_id',
    item_id     int unsigned                               not null comment '对应oms_item的item_id',
    item_num    int unsigned     default '1'               not null comment '订单中包含此商品的数量',
    price       int unsigned     default '0'               not null comment '对应oms_item中的价格，而非总价值',
    create_by   varchar(20)      default 'unknown_user'    null comment '创建者',
    create_time datetime         default CURRENT_TIMESTAMP not null comment '创建时间',
    update_by   varchar(20)      default 'unknown_user'    null comment '修改者',
    update_time datetime         default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint unsigned default '0'               not null comment '是否删除，0表示未删除，1表示删除',
    primary key (item_id, order_id)
)
    comment '订单详情，含商品信息';


````

等考完试需要优化我再上，忙忙忙

主要是没时间了要期末考试了 ; (