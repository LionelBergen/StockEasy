package web.cart

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.openqa.selenium.Alert
import web.helpfiles.WebTestBase

/**
 * Tests for the 'Cart' page
 */
class CartWebTestCartPage : CartWebTestBase() {
    // When going to an authenticated page without authentication, redirect to the home page
    @Test
    fun storeNotLoggedInTest() {
        getUrl("cart")
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, WebTestBase.driver.title)
        Assertions.assertFalse(WebTestBase.driver.currentUrl.contains("store"))
    }

    @Test
    fun cartViewEmptyCartTest() {
        login(USER_EMPTY_CART)
        clickElementById("shopping-cart")
        Assertions.assertEquals("View Cart", WebTestBase.driver.title)
        assertPageContainsText("Your cart is empty.")

        // test the go back home link
        clickElementById("home")
        Assertions.assertTrue(WebTestBase.driver.currentUrl.contains("store"))
    }

    @Test
    fun cartViewTest() {
        login(USER_WITH_CART)

        clickElementById("shopping-cart")
        Assertions.assertEquals("View Cart", WebTestBase.driver.title)

        // assert table rows (minus quantity)
        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_1 12 Test Vendor $25.00 remove from cart",
            "BeerProduct_2 6 Test Vendor $12.50 remove from cart",
            "Product From Another vendor Variant 1 Another Vendor $55.55"), true)

        // assert quantites
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[1]", "4")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[2]", "1")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[3]", "8")

        // ensure the home link takes us home
        clickElementById("home")
        Assertions.assertTrue(WebTestBase.driver.currentUrl.contains("store"))
    }

    @Test
    fun clearCartButtonTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        assertTableRows(
            PRODUCT_TABLE_ID,
            listOf("BeerProduct_1 12 Test Vendor $25.00 remove from cart", "BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"),
            true
        )
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[1]", "4")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[2]", "1")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[3]", "8")

        clickElementById("deleteCart")

        // dismiss the popup
        val alertOK: Alert = driver.switchTo().alert()
        alertOK.dismiss()

        // ensure nothing has changed
        assertTableRows(
            PRODUCT_TABLE_ID,
            listOf("BeerProduct_1 12 Test Vendor $25.00 remove from cart", "BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"),
            true
        )

        // actually go through with deleting cart
        clickElementById("deleteCart")
        alertOK.accept()

        // ensure we're back home with nothing in our cart
        Assertions.assertTrue(WebTestBase.driver.currentUrl.contains("store"))
        Assertions.assertEquals(0, getNumberOfItemsInCart())
    }

    @Test
    fun changeQuantityTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[1]", "4")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[2]", "1")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[3]", "8")

        enterInputFieldByXPath("//input[@class='quantity quantity_cart'][1]", "89")
        enterInputFieldByXPath("(//input[@class='quantity quantity_cart'])[2]", "44")
        enterInputFieldByXPath("(//input[@class='quantity quantity_cart'])[3]", "10")

        assertInputValueWithWaitByXPath("//input[@class='quantity quantity_cart'][1]", "89")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[2]", "44")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[3]", "10")

        // Ensure the values are saved
        logout()
        login(USER_WITH_CART)
        clickElementById("shopping-cart")
        assertInputValueWithWaitByXPath("//input[@class='quantity quantity_cart'][1]", "89")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[2]", "44")
        assertInputValueWithWaitByXPath("(//input[@class='quantity quantity_cart'])[3]", "10")
    }

    @Test
    fun removeProductFromCartTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_1 12 Test Vendor $25.00 remove from cart", "BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"), true)
        clickElementByxPath("//button[@class='remove_from_cart'][1]")
        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"), true)

        // Ensure changes are saved
        logout()
        login(USER_WITH_CART)
        clickElementById("shopping-cart")
        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"), true)
    }

    // Ensure we redirect back to home page after last item is removed
    @Test
    fun removeLastItemFromCartTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_1 12 Test Vendor $25.00 remove from cart", "BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"), true)
        clickElementByxPath("//button[@class='remove_from_cart'][1]")
        assertTableRows(PRODUCT_TABLE_ID, listOf("BeerProduct_2 6 Test Vendor $12.50 remove from cart", "Product From Another vendor Variant 1 Another Vendor $55.55"), true)
        clickElementByxPath("//button[@class='remove_from_cart'][1]")
        assertTableRows(PRODUCT_TABLE_ID, listOf("Product From Another vendor Variant 1 Another Vendor $55.55"), true)
        clickElementByxPath("//button[@class='remove_from_cart'][1]")

        assertCartVisibleWithWait()
        Assertions.assertEquals("Product Services Store", WebTestBase.driver.title)
        Assertions.assertEquals(0, getNumberOfItemsInCart())
    }

    @Test
    fun cartSubtotalTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        Assertions.assertEquals("$556.9", getElementTextValueByXPath("(//*[@id='cart_total'])[1]"))
        enterInputFieldByXPath("(//input[@class='quantity quantity_cart'])[1]", "4890")
        enterInputFieldByXPath("(//input[@class='quantity quantity_cart'])[2]", "8")
        enterInputFieldByXPath("(//input[@class='quantity quantity_cart'])[3]", "10")

        assertTextValueWithWaitByXPath("(//*[@id='cart_total'])[1]", "$122,905.5")

        // make sure removing an item affects the subtotal
        clickElementByxPath("//button[@class='remove_from_cart'][1]")
        assertTextValueWithWaitByXPath("(//*[@id='cart_total'])[1]", "$655.5")
    }

    // TODO: assert the view once we have a view we'll stick with
    @Test
    fun submitCartTest() {
        modifedTestData = true
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        clickElementById("submitCart")

        Assertions.assertEquals("Finalize Cart", WebTestBase.driver.title)

        clickElementById("submitCartFinal")
        val alertOK: Alert = driver.switchTo().alert()
        alertOK.accept()

        Assertions.assertTrue(WebTestBase.driver.currentUrl.contains("store"))
        assertNumberOfItemsInCartWithWait(0)

        // ensure the cart is actually saved
        logout()
        login(USER_WITH_CART)
        assertNumberOfItemsInCartWithWait(0)
    }

    // Ensure when we cancel submitting a cart, it does not get deleted
    @Test
    fun submitCancelCartTest() {
        login(USER_WITH_CART)
        clickElementById("shopping-cart")

        clickElementById("submitCart")

        Assertions.assertEquals("Finalize Cart", WebTestBase.driver.title)

        clickElementById("submitCartFinal")

        // dismiss the popup
        val alertOK: Alert = driver.switchTo().alert()
        alertOK.dismiss()

        Assertions.assertEquals("Finalize Cart", WebTestBase.driver.title)
        clickElementById("home")
        assertNumberOfItemsInCartWithWait(3)
    }

    @Test
    fun cancelCartTest() {
        login(USER_WITH_CART)
        clickElementById("shopping-cart")
        clickElementById("submitCart")

        clickElementById("home")

        assertNumberOfItemsInCartWithWait(3)
        Assertions.assertTrue(WebTestBase.driver.currentUrl.contains("store"))
    }
}
