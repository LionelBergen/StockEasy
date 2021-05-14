package dad.business.data

import dad.business.data.component.*
import dad.business.util.SystemVariables
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upperCase

val DATBASE_UTIL: DatabaseService = DatabaseService()

// TODO: consider using a recusive function such as http://sqlfiddle.com/#!17/3640f/2 or a way we can filter by category name while getting children, grandchildren etc
const val CATEGORY_SELECT = " SELECT category.id AS id, category.sortByValue AS sortByValue, 'CATEGORY' as type, category.name AS name, parent.name AS parent_name, parent.id AS parent_id "

// TODO: don't allow instantiations outside this class
// TODO: lots of refactoring can be done here
class DatabaseService {
    private val allStoreItems: List<StoreItem>

    init {
        Database.connect(
            SystemVariables.dbURL,
            driver = "org.postgresql.Driver",
            user = SystemVariables.dbUsername,
            password = SystemVariables.dbPassword
        )
        println("Initialized database connection!")

        this.allStoreItems = getAllStoreItems()
    }

    fun getUserByUsername(username: String): User? {
        return transaction {
            val result = UserTable.find { UserTables.username.upperCase() eq username.toUpperCase() }.limit(1)
                .with(UserTable::userTypes)
                .firstOrNull()

            return@transaction if (result != null) mapResultToUser(result) else null
        }
    }

    fun getAllStoreItems(): List<StoreItem> {
        // TODO: copying objects should be done elsewhere
        if (this.allStoreItems != null) {
            val copyOfList = ArrayList<StoreItem>()

            for (si in this.allStoreItems) {
                copyOfList.add(si.deepCopy())
            }

            return copyOfList
        }

        val categories = ArrayList<StoreItem>()

        return transaction {
            // get all categories, order by parent desc so we get null ones first (top-tree nodes first)
            // Get 'Vendors' as an additional category, with every vendor as a sub category
            // Products will be sorted alphavetically so sortByBalue is not important
            val sql = CATEGORY_SELECT +
                    " FROM category " +
                    " LEFT JOIN categories ON categories.child_category_id = category.id " +
                    " LEFT JOIN category parent ON parent.id = categories.parent_category_id " +
                    " UNION " +
                    " SELECT -20 AS id, 10000 AS sortByValue, 'VENDOR' as type, 'Vendors' AS name, null AS parent_name, null AS parent_id " +
                    " UNION " +
                    " SELECT V.id AS id, 10000 AS sortByValue, 'VENDORS' as type, V.name AS name, 'Vendors' AS parent_name, -20 AS parent_id " +
                    " FROM Vendor V " +
                    " ORDER BY parent_name DESC; "

            val pstmt = connection.prepareStatement(sql)
            pstmt.execute()

            // build the tree
            while (pstmt.resultSet.next()) {
                val id: Int = pstmt.resultSet.getInt("id")
                val type: StoreItemType = StoreItemType.valueOf(pstmt.resultSet.getString("type"))

                val name = pstmt.resultSet.getString("name")
                val parentName = pstmt.resultSet.getString("parent_name")
                val parentId: Int = pstmt.resultSet.getInt("parent_id")
                val sortByValue: Int? = pstmt.resultSet.getInt("sortByValue")

                val parentCategory = if (parentName == null) null else StoreItem(parentId, sortByValue, if (type == StoreItemType.CATEGORY) StoreItemType.CATEGORY else StoreItemType.VENDOR, parentName, listOf(), null)
                categories += StoreItem(id, sortByValue, type, name, listOf(), parentCategory)
            }

            return@transaction categories
        }
    }

    fun getProducts(search: String): Set<StoreProduct> {
        val searchString = '%' + search.toLowerCase() + '%'
        val SQL = " SELECT * FROM store_product WHERE LOWER(product_name) LIKE ? OR LOWER(category_name) LIKE ? OR LOWER(vendor) LIKE ? ORDER BY product_name;"

        return getProductsFromQuery(SQL, listOf(searchString, searchString, searchString))
    }

    fun getProducts(categoryId: Int): Set<StoreProduct> {
        val SQL = "SELECT * FROM store_product WHERE category_id = ? ORDER BY product_name; "

        return getProductsFromQuery(SQL, listOf(categoryId))
    }

    fun getProductsByVendor(vendorId: Int): Set<StoreProduct> {
        val SQL = "SELECT * FROM store_product WHERE vendor_id = ? ORDER BY product_name; "

        return getProductsFromQuery(SQL, listOf(vendorId))
    }

    fun getActiveCartForUser(user: User): Cart? {
        var result: Cart? = null

        val SQL = getSQLForCartWithJoins() +
            " WHERE c.user_id = " + user.id +
            " AND CS.name = 'active' "

        return transaction {
            val preparedStatement = connection.prepareStatement(SQL)
            preparedStatement.execute()

            while (preparedStatement.resultSet.next()) {
                val cartId: Int = preparedStatement.resultSet.getInt("cart_id")
                val cartStatus: String = preparedStatement.resultSet.getString("cart_status")
                val productName: String? = preparedStatement.resultSet.getString("product_name")

                if (result == null) {
                    result = Cart(cartId, CartStatus.valueOf(cartStatus.toUpperCase()), user.id, user.username, user.email, user.fullName, user.phoneNumber, listOf())
                }

                if (!productName.isNullOrEmpty()) {
                    val productId: Int = preparedStatement.resultSet.getInt("product_id")
                    val variant: String = preparedStatement.resultSet.getString("variant_name")
                    val variantId: Int = preparedStatement.resultSet.getInt("variant_id")
                    val price: Double = preparedStatement.resultSet.getDouble("price")
                    val vendorName: String = preparedStatement.resultSet.getString("vendor_name")
                    val vendorEmail: String = preparedStatement.resultSet.getString("vendor_email")
                    val vendorId: Int = preparedStatement.resultSet.getInt("vendor_id")
                    val quantity: Int = preparedStatement.resultSet.getInt("quantity")

                    // These won't be null if productName is not null
                    result!!.cartProducts += CartProduct(price, quantity, productName, productId, variant, variantId, vendorName, vendorEmail, vendorId)
                }
            }

            return@transaction result
        }
    }

    fun createNewCartForUser(user: User) {
        val sql = "INSERT INTO Cart(user_id) VALUES (${user.id});"

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.executeUpdate()
        }
    }

    fun findCartById(cartId: Int): Cart? {
        val sql = getSQLForCartWithJoins() +
                " WHERE C.id = $cartId "

        return transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
            if (!preparedStatement.resultSet.next()) {
                return@transaction null
            }

            val status: CartStatus = CartStatus.valueOf(preparedStatement.resultSet.getString("cart_status")!!.toUpperCase())
            val userId: Int = preparedStatement.resultSet.getInt("user_id")
            var cartProducts: List<CartProduct> = listOf()
            // Note: Those can be 'null', in which case Jetbrains returns '0'.
            var quantity: Int = preparedStatement.resultSet.getInt("quantity")
            var productId: Int = preparedStatement.resultSet.getInt("product_id")
            var variantId: Int = preparedStatement.resultSet.getInt("variant_id")
            val userName: String = preparedStatement.resultSet.getString("username")
            val userFullName: String? = preparedStatement.resultSet.getString("user_full_name")
            val userEmail: String? = preparedStatement.resultSet.getString("user_email")
            val userPhoneNumber: String? = preparedStatement.resultSet.getString("user_phone_number")

            val result = Cart(cartId, status, userId, userName, userEmail, userFullName, userPhoneNumber, cartProducts)

            if (productId != 0 && quantity != 0 && variantId != 0) {
                val productName: String = preparedStatement.resultSet.getString("product_name")
                val variant: String = preparedStatement.resultSet.getString("variant_name")
                val price: Double = preparedStatement.resultSet.getDouble("price")
                val vendorName: String = preparedStatement.resultSet.getString("vendor_name")
                val vendorEmail: String = preparedStatement.resultSet.getString("vendor_email")
                val vendorId: Int = preparedStatement.resultSet.getInt("vendor_id")

                cartProducts += CartProduct(price, quantity, productName, productId, variant, variantId, vendorName, vendorEmail, vendorId)
            }

            while (preparedStatement.resultSet.next()) {
                productId = preparedStatement.resultSet.getInt("product_id")
                val productName: String = preparedStatement.resultSet.getString("product_name")
                val variant: String = preparedStatement.resultSet.getString("variant_name")
                variantId = preparedStatement.resultSet.getInt("variant_id")
                val price: Double = preparedStatement.resultSet.getDouble("price")
                val vendorName: String = preparedStatement.resultSet.getString("vendor_name")
                val vendorEmail: String = preparedStatement.resultSet.getString("vendor_email")
                val vendorId: Int = preparedStatement.resultSet.getInt("vendor_id")
                quantity = preparedStatement.resultSet.getInt("quantity")

                cartProducts += CartProduct(price, quantity, productName, productId, variant, variantId, vendorName, vendorEmail, vendorId)
            }

            result.cartProducts = cartProducts

            return@transaction result
        }
    }

    fun addNewProductToCart(quantity: Int, productId: Int, variantId: Int, cart: Cart) {
        val sql = "INSERT INTO cart_product(cart_id, product_id, variant_id, quantity) " +
                "VALUES (${cart.id}, $productId, $variantId, $quantity);"

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
        }
    }

    fun updateQuantityOfCartProduct(newQuantity: Int, productId: Int, variantId: Int, cart: Cart) {
        val sql = "UPDATE cart_product SET quantity = $newQuantity WHERE cart_id = ${cart.id} AND product_id = $productId AND variant_id = $variantId "

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
        }
    }

    fun deleteProductFromCart(productId: Int, variantId: Int, cartId: Int) {
        val sql = "DELETE FROM cart_product WHERE cart_id = $cartId AND product_id = $productId AND variant_id = $variantId "

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
        }
    }

    fun deleteUserCart(cartId: Int, status: CartStatus) {
        val sql = "UPDATE cart SET cart_status = ${status.id} WHERE id = $cartId"

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
        }
    }

    fun insertSignInAttempt(username: String, password: String?, ipAddress: String, successful: Boolean) {
        val sql = "INSERT INTO sign_in_attempt(username, password, ip_address, successful) VALUES ('$username', '$password', '$ipAddress', $successful);"

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()
        }
    }

    fun getAllUsers() : List<User> {
        var results = listOf<User>()
        val sql = "SELECT id, username, email, full_name, phone_number FROM \"public\".user WHERE username!= 'admin'; "

        transaction {
            val preparedStatement = connection.prepareStatement(sql)
            preparedStatement.execute()

            while (preparedStatement.resultSet.next()) {
                val id: Int = preparedStatement.resultSet.getInt("id")
                val username: String = preparedStatement.resultSet.getString("username")
                val email: String? = preparedStatement.resultSet.getString("email")
                val fullName: String? = preparedStatement.resultSet.getString("full_name")
                val phoneNumber: String? = preparedStatement.resultSet.getString("phone_number")

                results += User(id, username, "", email, fullName, phoneNumber, listOf(), listOf())
            }
        }

        return results
    }

    fun insertStore() {

    }

    private fun mapResultToUser(result: UserTable): User {
        return User(
            result.id.value,
            result.username,
            result.password,
            result.email,
            result.fullName,
            result.phoneNumber,
            result.userTypes.map { UserType.fromValue(it.userType) },
            result.stores.map { StoreWithCategoryIds(it.name, mapResultToCategories(it.categories)) }
        )
    }

    private fun mapResultToCategories(result: SizedIterable<CategoryTable>): List<Int> {
        return result.map {
            it.id.value
        }
    }

    private fun getSQLForCartWithJoins(): String {
        return " SELECT C.id AS cart_id, C.user_id AS user_id, U.username, U.full_name AS user_full_name, U.email AS user_email, U.phone_number AS user_phone_number, CS.name AS cart_status, P.name AS product_name, V.name AS variant_name, VP.price, " +
                " Ve.name AS vendor_name, Ve.email AS vendor_email, quantity, P.id AS product_id, V.id AS variant_id, VP.vendor_id AS vendor_id " +
                " FROM Cart C " +
                " JOIN cart_status CS ON CS.id = C.cart_status " +
                " JOIN \"user\" U ON U.id = C.user_id " +
                " LEFT JOIN cart_product CP ON CP.cart_id = C.id " +
                " LEFT JOIN product P ON P.id = CP.product_id " +
                " LEFT JOIN variant V ON v.id = CP.variant_id " +
                " LEFT JOIN vendor_product VP ON VP.variant_id = V.id " +
                " LEFT JOIN vendor Ve ON Ve.id = VP.vendor_id "
    }

    private fun getProductsFromQuery(sqlQuery: String, params: List<Any>): Set<StoreProduct> {
        val results = LinkedHashSet<StoreProduct>()

        return transaction {
            val preparedStatement = connection.prepareStatement(sqlQuery)

            params.forEachIndexed { index, element ->
                preparedStatement.setObject(index + 1, element)
            }

            preparedStatement.execute()

            while (preparedStatement.resultSet.next()) {
                val id: Int = preparedStatement.resultSet.getInt("id")
                val productName: String = preparedStatement.resultSet.getString("product_name")
                val variantName: String = preparedStatement.resultSet.getString("variant")
                val variantId: Int = preparedStatement.resultSet.getInt("variant_id")
                val price: Double = preparedStatement.resultSet.getDouble("price")
                val vendorEmail: String = preparedStatement.resultSet.getString("vendor_email")
                val categoryId: Int = preparedStatement.resultSet.getInt("category_id")
                val categoryName: String = preparedStatement.resultSet.getString("category_name")
                val variant = Variant(variantId, variantName, price)
                val category = Category(categoryId, categoryName)

                // TODO: category
                if (results.map { it.id }.contains(id)) {
                    // duplicates don't matter, both are sets
                    results.filter { it.id == id }.get(0).variants += variant
                    results.filter { it.id == id }.get(0).categories += category
                } else {
                    results += StoreProduct(id, productName, setOf(category), setOf(variant), vendorEmail)
                }
            }

            return@transaction results
        }
    }
}
