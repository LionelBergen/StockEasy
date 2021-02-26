/* TODO: drop & recreate database instead */
DROP VIEW IF EXISTS  "store_product";
DROP TABLE IF EXISTS "user_user_type";
DROP TABLE IF EXISTS "user_type";
DROP TABLE IF EXISTS "user_store";
DROP TABLE IF EXISTS "store_category";
DROP TABLE IF EXISTS "store";
DROP TABLE IF EXISTS "cart_product";
DROP TABLE IF EXISTS "cart";
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "categories";
DROP TABLE IF EXISTS "product_categories";
DROP TABLE IF EXISTS "vendor_product";
DROP TABLE IF EXISTS "variant";
DROP TABLE IF EXISTS "vendor";
DROP TABLE IF EXISTS "product";
DROP TABLE IF EXISTS "category";
DROP TABLE IF EXISTS "cart_status";
DROP TABLE IF EXISTS "sign_in_attempt";

DEALLOCATE ALL;

CREATE TABLE "store"(id SERIAL PRIMARY KEY, name VARCHAR(255) UNIQUE);
CREATE TABLE "user"(id SERIAL PRIMARY KEY, username VARCHAR(255) UNIQUE, password VARCHAR(255), email VARCHAR(255), full_name VARCHAR(255), phone_number VARCHAR(255));
CREATE TABLE "user_type"(id SERIAL PRIMARY  KEY, user_type VARCHAR(255) UNIQUE);
CREATE TABLE "user_user_type"(user_type_id INTEGER, user_id INTEGER, PRIMARY KEY(user_type_id, user_id));
CREATE TABLE "user_store"(user_id INTEGER, store_id INTEGER, PRIMARY KEY(user_id, store_id));
CREATE TABLE "vendor"(id SERIAL PRIMARY KEY, name VARCHAR(255) UNIQUE, email VARCHAR(255) NOT NULL);
CREATE TABLE "variant"(id SERIAL PRIMARY KEY, product_id INTEGER, price decimal(6, 2), name VARCHAR(255));
CREATE TABLE "category"(id SERIAL PRIMARY KEY, name VARCHAR(255), sortByValue INTEGER NOT NULL DEFAULT 100);
CREATE TABLE "categories"(parent_category_id INTEGER, child_category_id INTEGER, PRIMARY KEY(parent_category_id, child_category_id));
CREATE TABLE "product"(id SERIAL PRIMARY KEY, name VARCHAR(255));
CREATE TABLE "product_categories"(parent_category_id INTEGER, child_product_id INTEGER, PRIMARY KEY(parent_category_id, child_product_id));
CREATE TABLE "store_category"(category_id INTEGER, store_id INTEGER, PRIMARY KEY(category_id, store_id));
CREATE TABLE "vendor_product"(id SERIAL PRIMARY KEY, price decimal(6, 2), variant_id INTEGER NOT NULL, vendor_id INTEGER NOT NULL, item_code VARCHAR(255), upc VARCHAR(255));
CREATE TABLE cart(id SERIAL PRIMARY KEY, cart_status INTEGER NOT NULL DEFAULT 1, user_id INTEGER NOT NULL, date_created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, date_ended TIMESTAMP);
CREATE TABLE cart_status(id INTEGER PRIMARY KEY, name VARCHAR(25) UNIQUE NOT NULL);
CREATE TABLE cart_product(cart_id INTEGER, product_id INTEGER, variant_id INTEGER, quantity INTEGER NOT NULL DEFAULT 1, PRIMARY KEY(cart_id, product_id, variant_id));
CREATE TABLE sign_in_attempt(id SERIAL PRIMARY KEY, username VARCHAR(255), password VARCHAR(255), ip_address VARCHAR(255), successful BOOLEAN, date_inserted TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

/** Foreign keys **/
ALTER TABLE "user_user_type" ADD FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE "user_user_type" ADD FOREIGN KEY (user_type_id) REFERENCES "user_type" (id);
ALTER TABLE "user_store" ADD FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE "user_store" ADD FOREIGN KEY (store_id) REFERENCES "store" (id);
ALTER TABLE "categories" ADD FOREIGN KEY (parent_category_id) REFERENCES "category" (id);
ALTER TABLE "categories" ADD FOREIGN KEY (child_category_id) REFERENCES "category" (id);
ALTER TABLE "product_categories" ADD FOREIGN KEY (parent_category_id) REFERENCES "category" (id);
ALTER TABLE "product_categories" ADD FOREIGN KEY (child_product_id) REFERENCES "product" (id);
ALTER TABLE "store_category" ADD FOREIGN KEY (category_id) REFERENCES "category" (id);
ALTER TABLE "store_category" ADD FOREIGN KEY (store_id) REFERENCES "store" (id);
ALTER TABLE "variant" ADD FOREIGN KEY (product_id) REFERENCES "product" (id);
ALTER TABLE "vendor_product" ADD FOREIGN KEY (variant_id) REFERENCES "variant" (id);
ALTER TABLE "vendor_product" ADD FOREIGN KEY (vendor_id) REFERENCES "vendor" (id);
ALTER TABLE "cart" ADD FOREIGN KEY (cart_status) REFERENCES "cart_status" (id);
ALTER TABLE "cart" ADD FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE "cart_product" ADD FOREIGN KEY (cart_id) REFERENCES "cart" (id);
ALTER TABLE "cart_product" ADD FOREIGN KEY (product_id) REFERENCES "product" (id);
ALTER TABLE "cart_product" ADD FOREIGN KEY (variant_id) REFERENCES "variant" (id);

INSERT INTO "user_type"(id, user_type) VALUES (1, 'admin');
INSERT INTO "user_type"(id, user_type) VALUES (2, 'store_owner');
INSERT INTO "user_type"(id, user_type) VALUES (3, 'store_rep');

INSERT INTO "cart_status"(id, name) VALUES (1, 'active');
INSERT INTO "cart_status"(id, name) VALUES (2, 'deleted');
INSERT INTO "cart_status"(id, name) VALUES (3, 'processed');

CREATE VIEW store_product AS SELECT P.id AS id,P.name AS product_name, va.id AS variant_id, C.id AS category_id, C.name AS category_name, va.name AS variant, VeP.price AS price, Ve.name AS vendor, Ve.id AS vendor_id, Ve.email AS vendor_email
	FROM Product P
	JOIN Variant Va ON Va.product_id = P.id
	JOIN vendor_product VeP ON VeP.variant_id = Va.id
	JOIN vendor Ve ON Ve.id = VeP.vendor_id
	JOIN product_categories PC ON PC.child_product_id = P.id
	JOIN category C ON C.id = PC.parent_category_id;