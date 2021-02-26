package web

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import web.helpfiles.WebTest

class ErrorPageWebTest : WebTest() {
    private val NOT_FOUND_TITLE = "Not Found"
    private val HOME_TITLE = "Product Services Login"
    private val BACK_HOME_LINK_ID = "homelink"

    @Test
    fun notFound404PageTest() {
        // get a url that does not exist
        getUrl("something/ggggg/gfjklgjdflgkjd")

        // Ensure we're on the not found page
        assertEquals(NOT_FOUND_TITLE, driver.title)

        // Ensure clicking the home button brings up home
        driver.findElement(By.id(BACK_HOME_LINK_ID)).click()
        assertEquals(HOME_TITLE, driver.title)
    }
}
