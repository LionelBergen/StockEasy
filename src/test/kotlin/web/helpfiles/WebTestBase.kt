package web.helpfiles

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import java.util.concurrent.TimeUnit

private val DRIVER: ChromeDriver = getInitializedDriver()
private val WEBPAGE_URL = "localhost:8080"
val LOGGED_IN_TITLE = "Product Services Store"

private fun getInitializedDriver(): ChromeDriver {
    WebDriverManager.chromedriver().setup()
    return ChromeDriver()
}

open class WebTestBase {
    companion object {
        val driver = DRIVER
        // Run at the beginning of each test class
        @BeforeAll
        @JvmStatic
        internal fun setup() {
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS)
        }
    }

    // Run before each test
    @BeforeEach
    private fun DRIVER() {
        DRIVER.get("localhost:8080")
    }

    @AfterEach
    private fun tearDown() {
        logout()
    }

    protected fun getUrl(url: String) {
        DRIVER.get("$WEBPAGE_URL/$url")
    }

    protected fun login(username: String) {
        driver.findElement(By.id("username")).sendKeys(username)
        driver.findElement(By.id("login_submit")).click()
    }

    protected fun getTableItemByText(tableElementId: String, elementText: String): WebElement {
        return driver.findElementById(tableElementId).findElement(By.xpath("//*[contains(text(), '$elementText')]"))
    }

    protected fun getTableItemByText(tableElement: WebElement, elementText: String): WebElement {
        return tableElement.findElement(By.xpath("//*[contains(text(), '$elementText')]"))
    }

    protected fun clickElementById(id: String) {
        val element: WebElement = driver.findElement(By.id(id))
        (driver as JavascriptExecutor).executeScript("arguments[0].click();", element)
    }

    protected fun clickElementByxPath(xPath: String) {
        val element: WebElement = driver.findElement(By.xpath(xPath))
        (driver as JavascriptExecutor).executeScript("arguments[0].click();", element)
    }

    /**
     * Logs the user out. Function is safe to run even if the user is not logged in
     */
    protected fun logout() {
        getUrl("/logout")
    }
}
