package web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import web.helpfiles.ProductPageWebTestHelper

// TODO: test 2 categories 1 product
// TODO: Ensure we filter products by store.
class ProductPageTest : ProductPageWebTestHelper() {
    @Test
    fun noStoresUserViewTest() {
        login(NO_PRODUCTS_USER)

        // ensure store&products table is not in view
        assertFalse(doesElementExist(STORE_TABLE_ID))
        assertFalse(doesElementExist(PRODUCTS_TABLE_ID))

        assertPageContainsText("Sorry you don't have any products")

        // ensure logout link works
        clickElementById("logoutLink")
        assertPageContainsText("Please sign in")
    }

    @Test
    fun emptyStoreViewTest() {
        login(EMPTY_STORE_USER)

        // ensure store&products table is not in view
        assertFalse(doesElementExist(STORE_TABLE_ID))
        assertFalse(doesElementExist(PRODUCTS_TABLE_ID))

        assertPageContainsText("Sorry you don't have any products")
    }

    // Asserts an initial table view
    @Test
    fun storeViewTest() {
        login(USER_WITH_PRODUCTS)

        assertPageDoesNotContainText("Sorry you don't have any products")
        assertElementNotVisibleByIdWithWait(PRODUCTS_TABLE_ID)

        // ensure we display the store name to the user
        assertEquals("Komodo Loco", getElementText("store_name"))
        // Ensure the categories are in the expected order
        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, true)

        // this element is used for building the store in JS
        assertElementNotVisibleByIdWithWait("store_container_complete")
        assertElementNotVisibleByIdWithWait("cart_complete")

        // Ensure 'vendors' category has class of 'vendors_category'
        assertTableAtDataContainsCssClass("Vendors", "vendors_category")

        // Ensure other categories do not contain 'vendors_category' class
        assertTableAtDataDoesNotContainCssClass("Frozen", "vendors_category")
    }

    // When clicking into a category with no children, don't do anything
    @Test
    fun storeEmptyCategoryTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))

        assertElementNotVisibleByIdWithWait("product_back")
        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, false)
    }

    // Tests viewing a product and all of it's inputs
    @Test
    fun storeProductsViewTest() {
        val expectedNumberOfProducts = 2
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        assertProductsTable(listOf("BeerProduct_1", "BeerProduct_2"), false)

        // ensure categories table not shown
        assertProductsTableVisible()

        assertEquals(expectedNumberOfProducts, driver.findElementsByClassName("quantity").size)
        assertEquals(expectedNumberOfProducts, driver.findElementsByClassName("variant_input").size)
        assertEquals(expectedNumberOfProducts, driver.findElementsByClassName("addToCartButton").size)

        // quantity defaults
        assertEquals("1", driver.findElementsByClassName("quantity")[0].getAttribute("value"))
        assertEquals("1", driver.findElementsByClassName("quantity")[1].getAttribute("value"))

        // assert default variant id's
        assertEquals("1", driver.findElementsByClassName("variant_input")[0].getAttribute("value"))
        assertEquals("3", driver.findElementsByClassName("variant_input")[1].getAttribute("value"))

        // cart functionality will be another test
    }

    @Test
    fun defaultQuantityTest() {
        login(USER_WITH_PRODUCTS)

        // go to a product view
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        assertEquals("1", getInputValueByXPath("//input[@class='quantity'][1]"))
        enterInputFieldByXPath("//input[@class='quantity'][1]", "100")
        assertEquals("100", getInputValueByXPath("//input[@class='quantity'][1]"))

        clickElementWithWait(driver.findElementById("product_back"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        // assert the default value is "1". Note: We use wait here since the value is set in JS
        assertInputValueWithWaitByXPath("//input[@class='quantity'][1]", "1")
    }

    @Test
    fun productPriceFieldTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))

        // assert defaults
        assertEquals("$50.00", getElementTextValueByXPath("(//td[@class='price'])[1]"))
        assertEquals("$12.50", getElementTextValueByXPath("(//td[@class='price'])[2]"))

        enterDropdownFieldByXPath("//select[@class='variant_input'][1]", 1)
        assertEquals("$25.00", getElementTextValueByXPath("(//td[@class='price'])[1]"))
        assertEquals("$12.50", getElementTextValueByXPath("(//td[@class='price'])[2]"))
    }

    @Test
    fun storeBackButtonTest() {
        login(USER_WITH_PRODUCTS)

        // Since we havn't gone anywhere back button should not be present
        assertElementNotVisibleByIdWithWait("product_back")

        // should be visible after clicking an element
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        assertElementVisibleById("product_back")

        // go back to the main products view and ensure we don't see back button
        clickElementWithWait(driver.findElementById("product_back"))
        assertElementNotVisibleByIdWithWait("product_back")

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        assertElementVisibleById("product_back")

        clickElementWithWait(driver.findElementById("product_back"))
        clickElementWithWait(driver.findElementById("product_back"))
        assertElementNotVisibleByIdWithWait("product_back")
    }

    // Ensure back button works as expected when going into the products view
    @Test
    fun storeBackButtonProductTest() {
        login(USER_WITH_PRODUCTS)

        // go to products page and asset the back button is present
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))
        assertElementVisibleById("product_back")

        // click back button, ensure we don't see products
        clickElementWithWait(driver.findElementById("product_back"))
        assertCategoriesTableVisible()
        assertElementVisibleById("product_back")

        // go back to products
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "BeerProducts"))
        assertProductsTable(listOf("BeerProduct_1", "BeerProduct_2"), false)
        assertElementVisibleById("product_back")

        // go back to home
        clickElementWithWait(driver.findElementById("product_back"))
        clickElementWithWait(driver.findElementById("product_back"))
        clickElementWithWait(driver.findElementById("product_back"))

        // back to home page, ensure back button does not appear
        assertElementNotVisibleByIdWithWait("product_back")
    }

    // Ensure when we select an empty category the back button still works
    @Test
    fun storeBackButtonEmptyCategoryTest() {
        login(USER_WITH_PRODUCTS)

        // click an element that has no children a bunch of times
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))

        driver.findElementById("product_back").click()
        assertElementNotVisibleByIdWithWait("product_back")
    }

    // Ensure back button clears the search entry and shows default home page
    @Test
    fun storeBackButtonSearchTest() {
        login(USER_WITH_PRODUCTS)

        assertCategoriesTableVisible()
        enterInputFieldById("product_search", "beer")
        assertProductsTableVisible()
        clickElementWithWait(driver.findElementById("product_back"))

        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, false)
        assertInputValueWithWaitById("product_search", "")
    }

    // Test a single category & single product. We have a lot of logic in our JS regaridng a single child.
    // Also extensivley test he product_back button with the 1-child categories
    @Test
    fun singleProductViewTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Miller"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Miller_1"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Miller_2"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Miller_3"))

        assertProductsTable(listOf("Miller Single"), true)

        driver.findElementById("product_back").click()
        assertTableRows(STORE_TABLE_ID, listOf("Miller_3"), true)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Miller_3"))
        assertProductsTable(listOf("Miller Single"), true)

        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("Miller_3"), true)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("Miller_2"), true)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("Miller_1"), true)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("BeerProducts", "Miller"), false)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("Wine", "Beer"), false)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, false)
        assertElementNotVisibleByIdWithWait("product_back")
    }

    // Category with no children, not on main page
    @Test
    fun subBlankCategoryTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated_child_1"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated_child_2"))

        // should have no affect
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated_child_2"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated_child_2"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Refrigerated_child_2"))

        assertTableRows(STORE_TABLE_ID, listOf("Refrigerated_child_2"), true)

        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, listOf("Refrigerated_child_1"), true)
        clickElementWithWait(driver.findElementById("product_back"))
        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, false)
        assertElementNotVisibleByIdWithWait("product_back")
    }

    // Ensure we sort by product name on the products page
    @Test
    fun productSortedByNameTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Canned/Dry"))
        assertProductsTable(listOf("AA Product 1", "AB Product 2", "B Product 3", "C Z Product 4", "DA Product 5", "ZZ Product 6"), true)
    }

    // Ensure the search bar is only shown when on the first page or search page
    @Test
    fun searchBarHiddenTest() {
        login(USER_WITH_PRODUCTS)

        // Ensure it reappears on front page, not just on 'back'
        assertElementVisibleById("product_search")
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Alcohol"))
        assertElementNotVisibleByIdWithWait("product_search")
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Beer"))
        assertElementNotVisibleByIdWithWait("product_search")
        clickElementWithWait(driver.findElementById("product_back"))
        assertElementNotVisibleByIdWithWait("product_search")
        clickElementWithWait(driver.findElementById("product_back"))
        assertElementVisibleById("product_search")

        // Ensure products page doesn't show it either
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Canned/Dry"))
        assertElementNotVisibleByIdWithWait("product_search")
        clickElementWithWait(driver.findElementById("product_back"))
        assertElementVisibleById("product_search")
    }

    // Ensure we don't hide search bar when we click a category with no products
    @Test
    fun searchBarHiddenEmptyCategoryTest() {
        login(USER_WITH_PRODUCTS)

        assertElementVisibleById("product_search")
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Frozen"))
        assertElementVisibleById("product_search")
    }

    @Test
    fun searchTest() {
        login(USER_WITH_PRODUCTS)

        enterInputFieldById("product_search", "beer")
        assertProductsTable(listOf("BeerProduct_1", "BeerProduct_2"), true)
        assertProductsTableVisible()
        assertElementVisibleById("product_search")

        clearInputFieldById("product_search")
        assertCategoriesTableVisible()
        assertElementVisibleById("product_search")
    }

    @Test
    fun searchNoResultsTest() {
        login(USER_WITH_PRODUCTS)

        enterInputFieldSlowlyById("product_search", "Zgdlgjslg")
        assertTableEmptyById(PRODUCTS_TABLE_ID)
    }

    @Test
    fun searchByTest() {
        login(USER_WITH_PRODUCTS)

        // by product name, case insensetive
        enterInputFieldById("product_search", "bEeR")
        assertProductsTable(listOf("BeerProduct_1", "BeerProduct_2"), true)

        // by vendor, case insensetive
        enterInputFieldById("product_search", "Another vEndOr")
        assertProductsTable(listOf("Product From Another vendor"), true)

        // by Category name, case insensetive
        enterInputFieldById("product_search", "CaNNed/Dry")
        assertProductsTable(listOf("AA Product 1", "AB Product 2", "B Product 3", "C Z Product 4", "DA Product 5", "ZZ Product 6"), true)
    }

    // Test for SQL injection & strange characters in the search
    @Test
    fun searchSpecialCharactersTest() {
        login(USER_WITH_PRODUCTS)

        enterInputFieldById("product_search", "'//")
        assertTableEmptyById(PRODUCTS_TABLE_ID)

        enterInputFieldById("product_search", "~$#%||.||%'\"{}@^**$%")
        assertTableEmptyById(PRODUCTS_TABLE_ID)

        // ensure nothing broke
        clearInputFieldById("product_search")
        assertTableRows(STORE_TABLE_ID, DEFAULT_CATEGORIES_IN_ORDER, false)
    }

    // Test our special vendors 'category'
    @Test
    fun vendorsTest() {
        login(USER_WITH_PRODUCTS)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Vendors"))
        assertTableRows(STORE_TABLE_ID, listOf("Another Vendor", "Test Vendor"), true)

        clickElementWithWait(getTableItemByText(STORE_TABLE_ID, "Test Vendor"))
        assertProductsTable(listOf("AA Product 1", "AB Product 2", "B Product 3", "BeerProduct_1", "BeerProduct_2", "C Z Product 4", "DA Product 5", "Miller Single", "ZZ Product 6"), true)
    }
}
