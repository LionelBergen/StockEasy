package web

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import web.helpfiles.LOGGED_IN_TITLE
import web.helpfiles.WebTest

class HomePageLoginTest : WebTest() {
    private val USER_MULTI_USER_TYPE = "Multi User Types"
    private val NOT_LOGGED_IN_TITLE = "Product Services Login"
    private val INVALID_LOGIN_MESSAGE = "Invalid credentials"

    private val KOMODO_USER = "Komodo Loco"

    @Test
    fun loginMultiUserType() {
        getUrl("")

        // Ensure we're on the not found page
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, driver.title)

        // Ensure clicking the home button brings up home
        login(USER_MULTI_USER_TYPE)

        // Ensure when we go back we're still logged in
        getUrl("")
        Assertions.assertEquals(LOGGED_IN_TITLE, driver.title)

        // Ensure we show the store name, not user name
        Assertions.assertTrue(driver.findElementByTagName("body").text.contains("Komodo Loco"))
    }

    @Test
    fun loginCaseInsensetiveTest() {
        // Ensure login suceedes despite wrong case
        login("kOmOdO LOCO")
    }

    @Test
    fun invalidLoginTest() {
        driver.findElement(By.id("username")).sendKeys("someUsernameThatDoesNotExist")
        driver.findElement(By.id("login_submit")).click()

        // assert we're logged out
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, driver.title)

        // ensure an error is shown
        Assertions.assertTrue(driver.findElementByTagName("body").text.contains(INVALID_LOGIN_MESSAGE))

        // empty login
        driver.findElement(By.id("username")).sendKeys("")
        driver.findElement(By.id("login_submit")).click()
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, driver.title)
        Assertions.assertTrue(driver.findElementByTagName("body").text.contains(INVALID_LOGIN_MESSAGE))
    }

    @Test
    fun logoutTest() {
        getUrl("")

        // login then logout
        login(KOMODO_USER)
        logout()

        // assert we're logged out
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, driver.title)
    }

    // When going to an authenticated page without authentication, redirect to the home page
    @Test
    fun storeNotLoggedInTest() {
        logout()

        getUrl("store")
        Assertions.assertEquals(NOT_LOGGED_IN_TITLE, driver.title)
        assertFalse(driver.currentUrl.contains("store"))
    }
}
