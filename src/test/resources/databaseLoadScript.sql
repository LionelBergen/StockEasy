/* data */
INSERT INTO "user"(id, username, password) VALUES (1, 'admin', '');
INSERT INTO "user"(id, username, password) VALUES (2, 'Komodo Loco', '');
INSERT INTO "user"(id, username, password) VALUES (3, 'Multi User Types', '');
INSERT INTO "user"(id, username, password) VALUES (4, 'No Stores', '');
INSERT INTO "user"(id, username, password) VALUES (5, 'empty Store', '');
INSERT INTO "user"(id, username, password, email, full_name, phone_number) VALUES (6, 'Filled Cart User', '', 'testuser@gmail.com', 'Jeff Jefferson', '204-990-4688');
INSERT INTO "user"(id, username, password) VALUES (7, 'User with inactive carts', '');
INSERT INTO "user"(id, username, password) VALUES (8, 'User with an empty cart', '');

INSERT INTO "category"(id, name, sortByValue) VALUES(1, 'Alcohol', 1);
INSERT INTO "category"(id, name, sortByValue) VALUES(2, 'Frozen', 2);
INSERT INTO "category"(id, name, sortByValue) VALUES(3, 'Refrigerated', 2);
INSERT INTO "category"(id, name, sortByValue) VALUES(4, 'Canned/Dry', 3);
INSERT INTO "category"(id, name, sortByValue) VALUES(5, 'Other', 55);
INSERT INTO "category"(id, name) VALUES(6, 'Beer');
INSERT INTO "category"(id, name) VALUES(7, 'Wine');
INSERT INTO "category"(id, name) VALUES(8, 'Miller');
INSERT INTO "category"(id, name) VALUES(9, 'Miller_1');
INSERT INTO "category"(id, name) VALUES(10, 'Miller_2');
INSERT INTO "category"(id, name) VALUES(11, 'Miller_3');
INSERT INTO "category"(id, name) VALUES(13, 'BeerProducts');
INSERT INTO "category"(id, name) VALUES(110, 'child_two_parents');
INSERT INTO "category"(id, name) VALUES(111, 'parent_1');
INSERT INTO "category"(id, name) VALUES(112, 'parent_2');
INSERT INTO "category"(id, name) VALUES(200, 'Refrigerated_child_1');
INSERT INTO "category"(id, name) VALUES(201, 'Refrigerated_child_2');

INSERT INTO Vendor(id, name, email) VALUES(0, 'Test Vendor', 'lionelbergen@live.com');
INSERT INTO Vendor(id, name, email) VALUES(1, 'Another Vendor', 'lionel.b.development@gmail.com');

INSERT INTO "product"(id, name) VALUES(1, 'BeerProduct_1');
INSERT INTO "product"(id, name) VALUES(2, 'BeerProduct_2');
INSERT INTO "product"(id, name) VALUES(3, 'Miller Single');
INSERT INTO "product"(id, name) VALUES(4, 'ZZ Product 6');
INSERT INTO "product"(id, name) VALUES(5, 'AB Product 2');
INSERT INTO "product"(id, name) VALUES(6, 'B Product 3');
INSERT INTO "product"(id, name) VALUES(7, 'DA Product 5');
INSERT INTO "product"(id, name) VALUES(8, 'C Z Product 4');
INSERT INTO "product"(id, name) VALUES(9, 'AA Product 1');
INSERT INTO "product"(id, name) VALUES(10, 'Product From Another vendor');

INSERT INTO Variant(id, product_id, price, name) VALUES(1, 1, null, '24');
INSERT INTO Variant(id, product_id, price, name) VALUES(2, 1, null, '12');
INSERT INTO Variant(id, product_id, price, name) VALUES(3, 2, null, '6');
INSERT INTO Variant(id, product_id, price, name) VALUES(4, 3, null, 'King can');
INSERT INTO Variant(id, product_id, price, name) VALUES(5, 4, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(6, 5, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(7, 6, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(8, 7, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(9, 8, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(10, 9, null, 'Variant 1');
INSERT INTO Variant(id, product_id, price, name) VALUES(11, 10, 50.50, 'Variant 1');

INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(0, 50.0, 1, 0, '14116', '41516516afadsg');
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(1, 25.0, 2, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(2, 12.50, 3, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(3, 8.50, 4, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(4, 11.85, 5, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(5, 11.85, 6, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(6, 11.85, 7, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(7, 11.85, 8, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(8, 11.85, 9, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(9, 11.85, 10, 0, null, null);
INSERT INTO vendor_product(id, price, variant_id, vendor_id, item_code, upc) VALUES(10, 55.55, 11, 1, null, null);

INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(1, 6);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(1, 7);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(6, 8);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(8, 9);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(9, 10);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(10, 11);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(6, 13);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(111, 110);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(112, 110);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(3, 200);
INSERT INTO "categories"(parent_category_id, child_category_id) VALUES(200, 201);

INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(13, 1);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(13, 2);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(11, 3);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 4);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 5);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 6);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 7);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 8);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(4, 9);
INSERT INTO "product_categories"(parent_category_id, child_product_id) VALUES(5, 10);

INSERT INTO "user_user_type"(user_type_id, user_id) VALUES(2, 2);
INSERT INTO "user_user_type"(user_type_id, user_id) VALUES(1, 1);
INSERT INTO "user_user_type"(user_type_id, user_id) VALUES(2, 3);
INSERT INTO "user_user_type"(user_type_id, user_id) VALUES(3, 3);

INSERT INTO "store"(id, name) VALUES (1, 'Komodo Loco');
INSERT INTO "store"(id, name) VALUES (2, 'Empty Store');

INSERT INTO "store_category"(category_id, store_id) VALUES (1, 1);
INSERT INTO "store_category"(category_id, store_id) VALUES (2, 1);
INSERT INTO "store_category"(category_id, store_id) VALUES (3, 1);
INSERT INTO "store_category"(category_id, store_id) VALUES (4, 1);
INSERT INTO "store_category"(category_id, store_id) VALUES (5, 1);

INSERT INTO "user_store"(user_id, store_id) VALUES (2, 1);
INSERT INTO "user_store"(user_id, store_id) VALUES (6, 1);
INSERT INTO "user_store"(user_id, store_id) VALUES (3, 1);
INSERT INTO "user_store"(user_id, store_id) VALUES (5, 2);
INSERT INTO "user_store"(user_id, store_id) VALUES (7, 1);

INSERT INTO "cart"(id, user_id, cart_status) VALUES(0, 6, 2);
INSERT INTO "cart"(id, user_id) VALUES(1, 6);
INSERT INTO "cart"(id, user_id, cart_status) VALUES(2, 7, 2);
INSERT INTO "cart"(id, user_id, cart_status) VALUES(3, 7, 3);
INSERT INTO "cart"(id, user_id, cart_status) VALUES(4, 8, 1);

INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(1, 1, 2, 4);
INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(1, 2, 3, 1);
INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(1, 10, 11, 8);

INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(0, 1, 2, 55);
INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(0, 2, 3, 100);

INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(2, 1, 2, 15);
INSERT INTO "cart_product"(cart_id, product_id, variant_id, quantity) VALUES(3, 2, 3, 10);