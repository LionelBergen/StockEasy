package web.admin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import web.helpfiles.WebTest

class AdminMainPageWebTest : WebTest() {
    private val NON_ADMIN_USER = "Komodo Loco"
    private val HOME_TITLE = "Product Services Login"
    private val STORE_TITLE = "Product Services Store"
    private val ADMIN_TITLE = "Administration"

    private val ADMIN_USER = "Admin"

    // Ensure we can't access the admin page with non-admin users
    @Test
    fun invalidUserAdminPageTest() {
        // not logged in, should be redirected to login
        getUrl("admin")
        assertEquals(HOME_TITLE, driver.title)

        // Login using non-admin user. should redirect back to store
        login(NON_ADMIN_USER)
        getUrl("admin")
        assertEquals(STORE_TITLE, driver.title)
    }

    @Test
    fun adminPageTest() {
        login(ADMIN_USER)
        assertEquals(ADMIN_TITLE, driver.title)
    }
}
