package data

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.Cart
import dad.business.data.component.CartStatus
import dad.business.data.component.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// TODO: ensure test names match & all methods are tested
class DatabaseServiceTest {
    // TODO: write a parent class for this. It's needed for using 'transaction {', in order to rollback tests that insert/update the database
    init {
        Database.connect(
            "jdbc:postgresql://localhost:5432/dad_business_to_business",
            driver = "org.postgresql.Driver",
            user = "dad",
            password = "business"
        )
        println("Initialized test database connection")
    }

    private val NOT_FOUND_TITLE = "Not Found"
    private val HOME_TITLE = "Product Services Login"
    private val BACK_HOME_LINK_ID = "homelink"

    @Test
    fun getAllCategoriesTest() {
        val results = DATBASE_UTIL.getAllStoreItems()

        Assertions.assertNotNull(results)
        Assertions.assertEquals(21, results.size)
    }

    @Test
    fun getProductsTest() {
        val results = DATBASE_UTIL.getProducts(4)

        Assertions.assertNotNull(results)

        Assertions.assertEquals(6, results.size)
        Assertions.assertEquals(1, results.find { it.categories.find { it.name == "Canned/Dry" } != null }!!.variants.size)
        Assertions.assertEquals(1, results.find { it.categories.find { it.name == "Canned/Dry" } != null }!!.categories.size)
    }

    @Test
    fun getUserCartTest() {
        val result = DATBASE_UTIL.getActiveCartForUser(createTestUser(6))

        Assertions.assertNotNull(result)
        Assertions.assertNotNull(result!!.cartProducts)
        Assertions.assertEquals(3, result.cartProducts.size)
    }

    // Test a non-existent user
    @Test
    fun getUserCartEmptyCartTest() {
        // non existent user
        val result = DATBASE_UTIL.getActiveCartForUser(createTestUser(99898895))

        Assertions.assertNull(result)
    }

    // Test a user who has carts, but no active ones
    @Test
    fun getUserCartInactiveCartTest() {
        val result = DATBASE_UTIL.getActiveCartForUser(createTestUser(7))

        Assertions.assertNull(result)
    }

    @Test
    fun createNewCartTest() {
        transaction {
            try {
                val testUser: User = createTestUser(4)

                Assertions.assertNull(DATBASE_UTIL.getActiveCartForUser(testUser))
                DATBASE_UTIL.createNewCartForUser(testUser)
                val result = DATBASE_UTIL.getActiveCartForUser(testUser)

                Assertions.assertNotNull(result)
                Assertions.assertEquals(4, result!!.userId)
            } finally {
                rollback()
            }
        }
    }

    // test cart with products
    @Test
    fun getCartByIdTest() {
        val existingCartId = 1
        val result: Cart? = DATBASE_UTIL.findCartById(existingCartId)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(CartStatus.ACTIVE, result!!.status)
        Assertions.assertEquals(3, result.cartProducts.size)
    }

    // test cart with no products
    @Test
    fun getCartByIdEmptyCartTest() {
        val existingCartId = 4
        val result: Cart? = DATBASE_UTIL.findCartById(existingCartId)

        Assertions.assertNotNull(result)
        Assertions.assertEquals(CartStatus.ACTIVE, result!!.status)
        Assertions.assertEquals(0, result.cartProducts.size)
    }

    // test non-exstent cart id
    @Test
    fun getCartByIdNonExsistentCartTest() {
        val existingCartId = 9455
        val result: Cart? = DATBASE_UTIL.findCartById(existingCartId)

        Assertions.assertNull(result)
    }

    @Test
    fun addProductToCartTest() {
        val existingCartId = 4

        val cart: Cart? = DATBASE_UTIL.findCartById(existingCartId)
        Assertions.assertEquals(0, cart!!.cartProducts.size)
        transaction {
            try {
                DATBASE_UTIL.addNewProductToCart(55, 1, 2, cart)

                val resultCart: Cart? = DATBASE_UTIL.findCartById(existingCartId)
                Assertions.assertEquals(1, resultCart!!.cartProducts.size)
                Assertions.assertEquals(55, resultCart.cartProducts.get(0).quantity)
                Assertions.assertEquals(1, resultCart.cartProducts.get(0).productId)
                Assertions.assertEquals(2, resultCart.cartProducts.get(0).variantId)
                Assertions.assertEquals("12", resultCart.cartProducts.get(0).variantName)
                Assertions.assertEquals("BeerProduct_1", resultCart.cartProducts.get(0).productName)
                Assertions.assertEquals("Test Vendor", resultCart.cartProducts.get(0).vendorName)
            } finally {
                rollback()
            }
        }
    }

    @Test
    fun updateQuantityOfCartProductTest() {
        val existingCartId = 1
        var cart: Cart = DATBASE_UTIL.findCartById(existingCartId)!!

        transaction {
            try {
                Assertions.assertEquals(4, cart.cartProducts.find { it.productId == 1 && it.variantId == 2 }!!.quantity)
                DATBASE_UTIL.updateQuantityOfCartProduct(200, 1, 2, cart)
                cart = DATBASE_UTIL.findCartById(existingCartId)!!
                Assertions.assertEquals(200, cart.cartProducts.find { it.productId == 1 && it.variantId == 2 }!!.quantity)
            } finally {
                rollback()
            }
        }
    }

    @Test
    fun deleteProductFromCartTest() {
        val activeCartId = 1

        transaction {
            try {
                Assertions.assertEquals(3, DATBASE_UTIL.findCartById(activeCartId)!!.cartProducts.size)
                DATBASE_UTIL.deleteProductFromCart(2, 3, activeCartId)
                Assertions.assertEquals(2, DATBASE_UTIL.findCartById(activeCartId)!!.cartProducts.size)
                Assertions.assertEquals(1, DATBASE_UTIL.findCartById(activeCartId)!!.cartProducts.get(0).productId)
            } finally {
                rollback()
            }
        }
    }

    @Test
    fun deleteUserCartTest() {
        val activeCartId = 1

        transaction {
            try {
                Assertions.assertEquals(CartStatus.ACTIVE, DATBASE_UTIL.findCartById(activeCartId)!!.status)
                DATBASE_UTIL.deleteUserCart(activeCartId, CartStatus.DELETED)
                Assertions.assertEquals(CartStatus.DELETED, DATBASE_UTIL.findCartById(activeCartId)!!.status)
            } finally {
                rollback()
            }
        }
    }

    @Test
    fun getProductsBySearchStringTest() {
        val results = DATBASE_UTIL.getProducts("beer")

        Assertions.assertEquals(2, results.size)
        Assertions.assertEquals(2, results.find { it.id == 1 }!!.variants.size)
    }

    @Test
    fun getStoreProductsBySearchStringByCategoryTest() {
        val results = DATBASE_UTIL.getProducts("beeRProDucts")

        Assertions.assertEquals(2, results.size)
    }

    @Test
    fun getStoreProductsBySearchStringByVendorTest() {
        val results = DATBASE_UTIL.getProducts("test vendor")

        Assertions.assertEquals(9, results.size)
    }

    @Test
    fun getStoreProductsBySearchStringEmptyTest() {
        val results = DATBASE_UTIL.getProducts("doesnt exist gdfjlgksdkg")

        Assertions.assertEquals(0, results.size)
    }

    private fun createTestUser(userId: Int): User {
        return User(userId, "testUser", "", "testEmail123@gmail.com", "John Smith", "204-222-2222", listOf(), listOf())
    }

    // TODO: ensure these tests are tested in the productServiceTree
/*
    @Test
    fun getCategoriesByStoreTest() {
        val store : Store = getStoreGivenCategoryNames(listOf("Alcohol"))
        val results = DATBASE_UTIL.getCategoriesByStore(listOf(store))

        assertFalse(results.isEmpty())

        val firstResult = results.get(0)
        assertEquals("Alcohol", firstResult.name)

        val children = firstResult.children
        assertEquals(2, children.size)

        val beerCategory : Category? = children.find{ it.name == "Beer"}
        val wineCategory : Category? = children.find{ it.name == "Wine"}
        assertNotNull(beerCategory)
        assertNotNull(wineCategory)
        assertEquals(2, beerCategory!!.children.size)

        var millerCategory = beerCategory.children.get(0)
        assertNotNull(millerCategory)
        assertEquals(1, millerCategory.children.size)
        assertEquals("Miller", millerCategory.name)

        millerCategory = millerCategory.children.get(0)
        assertNotNull(millerCategory)
        assertEquals(1, millerCategory.children.size)
        assertEquals("Miller_1", millerCategory.name)

        millerCategory = millerCategory.children.get(0)
        assertNotNull(millerCategory)
        assertEquals(1, millerCategory.children.size)
        assertEquals("Miller_2", millerCategory.name)

        millerCategory = millerCategory.children.get(0)
        assertNotNull(millerCategory)
        assertEquals(0, millerCategory.children.size)
        assertEquals("Miller_3", millerCategory.name)

        // ensure we're filtering
        assertEquals(1, results.size)
    }

    @Test
    fun getCategoriesByStoreNoResultsTest() {
        val store : Store = getStoreGivenCategoryNames(listOf("Non Existant Table"))
        val results = DATBASE_UTIL.getCategoriesByStore(listOf(store))

        assertNotNull(results)
        assertTrue(results.isEmpty())
    }

    private fun getStoreGivenCategoryNames(categoryNames : List<String>) : Store {
        val categories : List<Category> = categoryNames.map { Category(null, it, listOf()) }

        return Store("", categories)
    }*/
}
