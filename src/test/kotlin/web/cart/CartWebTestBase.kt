package web.cart

import org.junit.jupiter.api.fail
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.support.ui.ExpectedConditions
import web.helpfiles.ModifiesDataTest

/**
 * Implements some helper functions for interacting with Cart
 */
open class CartWebTestBase : ModifiesDataTest() {
    private val SHOPPING_CART_ID = "shopping-cart"
    private val SHOPPING_CART_ITEMS_ATTRIBUTE = "data-count"

    protected val USER_WITH_CART = "Filled Cart User"
    protected val USER_INACTIVE_CART = "User with inactive carts"
    protected val USER_EMPTY_CART = "Komodo Loco"

    protected val NOT_LOGGED_IN_TITLE = "Product Services Login"
    protected val PRODUCT_TABLE_ID = "cart_product_table"
    protected val STORE_TABLE_ID = "store_table"

    protected fun assertCartVisibleWithWait() {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(SHOPPING_CART_ID)))
    }

    protected fun assertNumberOfItemsInCartWithWait(expectedNumberOfItems: Int) {
        var actualNumberOfItems  = -1
        try {
            getWait().until{
                actualNumberOfItems = driver.findElementById(SHOPPING_CART_ID).getAttribute(SHOPPING_CART_ITEMS_ATTRIBUTE).toInt()
                actualNumberOfItems == expectedNumberOfItems
            }
        } catch (e: TimeoutException) {
            fail("Cart did not contain expected number of items. expected $expectedNumberOfItems but was $actualNumberOfItems")
        }
    }

    protected fun getNumberOfItemsInCart(): Int {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(SHOPPING_CART_ID)))

        return driver.findElementById(SHOPPING_CART_ID).getAttribute(SHOPPING_CART_ITEMS_ATTRIBUTE).toInt()
    }

    protected fun clickAddToCartButtonAtIndex(index : Int) {
        clickElementByxPath("(//i[contains(@class, 'addToCartButton')])[${index}]")
    }

    protected fun getQuantityAtIndex(index : Int): String? {
        return getInputValueByXPath("(//input[contains(@class, 'quantity')])[${index}]")
    }

    protected fun enterQuantityAtIndex(index : Int, newValue : Int) {
        val quantityFieldXPath = "(//input[contains(@class, 'quantity')])[${index}]"
        enterInputFieldByXPath(quantityFieldXPath, newValue.toString())
        resetFocus()
        assertInputValueWithWaitByXPath(quantityFieldXPath, newValue.toString())
    }
}
