package web.cart

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Tests for 'products page', cart functionality
 */
class CartWebTestProductsPage : CartWebTestBase() {
    @Test
    fun cartTest() {
        login(USER_WITH_CART)
        assertCartVisibleWithWait()
        assertEquals(3, getNumberOfItemsInCart())
    }

    // Test a user that has no cart (0 items)
    @Test
    fun emptyCartTest() {
        login(USER_EMPTY_CART)
        assertCartVisibleWithWait()
        assertEquals(0, getNumberOfItemsInCart())
    }

    // Test a user with carts, but no active cart
    @Test
    fun expiredCartTest() {
        login(USER_INACTIVE_CART)
        assertCartVisibleWithWait()
        assertEquals(0, getNumberOfItemsInCart())
    }

    @Test
    fun addItemToCartTest() {
        modifedTestData = true
        login(USER_EMPTY_CART)
        assertNumberOfItemsInCartWithWait(0)
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        clickAddToCartButtonAtIndex(1)
        assertNumberOfItemsInCartWithWait(1)

        // adding the same product should have no affect on number of items in cart
        clickAddToCartButtonAtIndex(1)
        clickAddToCartButtonAtIndex(1)
        clickAddToCartButtonAtIndex(1)
        clickAddToCartButtonAtIndex(1)
        assertEquals(1, getNumberOfItemsInCart())

        clickAddToCartButtonAtIndex(2)
        assertNumberOfItemsInCartWithWait(2)
    }

    // ensure when we add an item to the cart, it automatically resets the quantity back to 1
    @Test
    fun addItemResetCounter() {
        modifedTestData = true
        login(USER_EMPTY_CART)
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        assertEquals("1", getQuantityAtIndex(1))
        enterQuantityAtIndex(1, 55)
        clickAddToCartButtonAtIndex(1)

        // ensure value reset back to 1
        assertEquals("1", getQuantityAtIndex(1))
    }

    // Ensure the cart is automatically updated
    @Test
    fun addItemAndClickCartTest() {
        modifedTestData = true
        login(USER_EMPTY_CART)
        assertNumberOfItemsInCartWithWait(0)
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        clickAddToCartButtonAtIndex(1);
        clickAddToCartButtonAtIndex(2);
        assertNumberOfItemsInCartWithWait(2)

        clickElementById("shopping-cart")
        assertPageDoesNotContainText("Your cart is empty")
    }
}
