package web.helpfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail
import org.openqa.selenium.By
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.support.ui.ExpectedConditions

open class ProductPageWebTestHelper : WebTest() {
    protected val NO_PRODUCTS_USER = "No Stores"
    protected val EMPTY_STORE_USER = "Empty Store"
    protected val STORE_TABLE_ID = "store_table"
    protected val PRODUCTS_TABLE_ID = "product_table"
    protected val USER_WITH_PRODUCTS = "Komodo Loco"
    protected val DEFAULT_CATEGORIES_IN_ORDER = listOf("Alcohol", "Frozen", "Refrigerated", "Canned/Dry", "Other", "Vendors")

    protected fun assertProductsTableVisible() {
        assertElementIsVisibleByXPathWithWait("//*[@id='$PRODUCTS_TABLE_ID']//tbody")
        assertElementNotVisibleByXPathWithWait("//*[@id='$STORE_TABLE_ID']//tbody")
    }

    protected fun assertCategoriesTableVisible() {
        assertElementIsVisibleByXPathWithWait("//*[@id='$STORE_TABLE_ID']//tbody")
        assertElementNotVisibleByXPathWithWait("//*[@id='$PRODUCTS_TABLE_ID']//tbody")
    }

    protected fun assertTableAtDataContainsCssClass(tableRowText : String, expectedCssClassName : String) {
        val actualCSSclasses = getElementClassNamesByXPath("//*[@id='${STORE_TABLE_ID}']//tbody//td[text()='${tableRowText}']")

        Assertions.assertTrue(actualCSSclasses.contains(expectedCssClassName), "value ${actualCSSclasses} did not contain ${expectedCssClassName}");
    }

    protected fun assertTableAtDataDoesNotContainCssClass(tableRowText : String, expectedCssClassName : String) {
        val actualCSSclasses = getElementClassNamesByXPath("//*[@id='${STORE_TABLE_ID}']//tbody//td[text()='${tableRowText}']")

        Assertions.assertFalse(actualCSSclasses.contains(expectedCssClassName), "value ${actualCSSclasses} DID contain ${expectedCssClassName}");
    }

    protected fun assertProductsTable(expectedProductNames : List<String>, orderMatters : Boolean) {
        val tableRowsPerProduct = 3;
        if (orderMatters) {
            for ((index) in expectedProductNames.withIndex()) {
                val productNameIndex = if (index == 0) 0 else index * tableRowsPerProduct;
                try {
                    getWait().until {
                        val elements = findTableRowsById(PRODUCTS_TABLE_ID)
                        try {
                            elements!![productNameIndex].text.contains(expectedProductNames[index])
                        } catch (e: StaleElementReferenceException) {
                            // ignore & retry
                        }
                    }
                } catch (e: TimeoutException) {
                    fail("Table expected value at $index to be ${expectedProductNames[index]} but was ${findTableRowsById(PRODUCTS_TABLE_ID)!![productNameIndex].text}")
                }
            }
        } else {
            for (value in expectedProductNames) {
                try {
                    getWait().until(
                        ExpectedConditions.textToBePresentInElement(
                            driver.findElement(By.id(PRODUCTS_TABLE_ID)),
                            value
                        )
                    )
                } catch (e: TimeoutException) {
                    fail("Table did not contain expected value $value")
                }
            }
        }

        Assertions.assertEquals(expectedProductNames.size * tableRowsPerProduct, findTableRowsById(PRODUCTS_TABLE_ID)!!.size)
    }
}
