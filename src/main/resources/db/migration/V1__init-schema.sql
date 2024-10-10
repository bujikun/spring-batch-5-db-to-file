create table if not exists `sales`
(
    `sale_id`        bigint primary key auto_increment,
    `product_id`     int            not null,
    `customer_id`    int            not null,
    `sale_date`      datetime       not null,
    `sale_amount`    decimal(10, 2) not null,
    `store_location` varchar(100)   not null,
    `country`        varchar(100)   not null,
    `processed`      bit            default 0

);

