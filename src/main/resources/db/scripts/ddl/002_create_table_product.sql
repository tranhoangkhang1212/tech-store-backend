CREATE TABLE product (
    id bigserial NOT NULL,
    name varchar(1024) NOT NULL,
    price integer NOT NULL,
    amount integer,
    currency varchar(255) NOT NULL,
    image_url varchar(255) NOT NULL,
    description varchar(2048),
    brand_id integer NOT NULL,
    star integer DEFAULT 1,
    promotion integer,
    is_sale boolean DEFAULT FALSE,
    sale_percent integer,
    created_at timestamp,
    created_by varchar(255),
    updated_at timestamp,
    updated_by varchar(255),
    version int8,

    primary key (id)
)