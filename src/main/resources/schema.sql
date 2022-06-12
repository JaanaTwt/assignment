DROP TABLE IF EXISTS products;

CREATE TABLE products
(
    id   INTEGER      NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS articles;

CREATE TABLE articles
(
    id    INTEGER      NOT NULL PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    stock INTEGER      NOT NULL DEFAULT 0,
    CHECK stock >= 0
);

DROP TABLE IF EXISTS products_with_articles;

CREATE TABLE products_with_articles
(
    product_id INTEGER NULL,
    article_id INTEGER NOT NULL,
    amount_of  INTEGER NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (article_id) REFERENCES articles (id)
);