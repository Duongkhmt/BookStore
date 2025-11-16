CREATE TABLE books
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    title          VARCHAR(255)          NOT NULL,
    author         VARCHAR(100)          NOT NULL,
    isbn           VARCHAR(20)           NOT NULL,
    price          DECIMAL(10, 2)        NOT NULL,
    stock_quantity INT                   NOT NULL,
    category_id    BIGINT                NOT NULL,
    publisher_id   BIGINT                NOT NULL,
    CONSTRAINT pk_books PRIMARY KEY (id)
);

ALTER TABLE books
    ADD CONSTRAINT uc_books_isbn UNIQUE (isbn);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES categories (id);

ALTER TABLE books
    ADD CONSTRAINT FK_BOOKS_ON_PUBLISHER FOREIGN KEY (publisher_id) REFERENCES publishers (id);