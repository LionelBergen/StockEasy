package web.helpfiles

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.fail
import org.openqa.selenium.*
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait

private const val DEFAULT_WAIT = 10L

open class WebTest : WebTestBase() {
    protected fun assertPageDoesNotContainText(expectedText: String) {
        Assertions.assertFalse(driver.findElementByTagName("body").text.contains(expectedText), "Page contained text: '$expectedText'")
    }

    protected fun assertPageContainsText(expectedText: String) {
        Assertions.assertTrue(driver.findElementByTagName("body").text.contains(expectedText), "Page did NOT contain text: '$expectedText'")
    }

    protected fun assertElementIsVisibleByXPathWithWait(xPath: String) {
        try {
            getWait().until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)))
        } catch (e: TimeoutException) {
            fail("Element was not visible: $xPath")
        }
    }

    protected fun assertElementNotVisibleByXPathWithWait(xPath: String) {
        try {
            getWait().until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xPath)))
        } catch (e: TimeoutException) {
            fail("Element was visible: $xPath")
        }
    }

    protected fun assertElementNotVisibleByIdWithWait(elementId: String) {
        try {
            getWait().until {
                !driver.findElementById(elementId).isDisplayed
            }
        } catch (e: TimeoutException) {
            fail("Element was visible: $elementId")
        }
    }

    protected fun assertElementIsVisibleByIdWithWait(elementId: String) {
        try {
            getWait().until {
                driver.findElementById(elementId).isDisplayed
            }
        } catch (e: TimeoutException) {
            fail("Element was not visible: $elementId")
        }
    }

    protected fun assertElementVisibleById(elementId: String) {
        Assertions.assertTrue(driver.findElementById(elementId).isDisplayed, "Element was NOT visible: $elementId")
    }

    protected fun getElementText(elementId: String): String {
        return driver.findElementById(elementId).text
    }

    protected fun doesElementExist(elementId: String): Boolean {
        return driver.findElementsById(elementId).size != 0
    }

    protected fun getElementClassNamesByXPath(xPath: String) : String {
        return driver.findElementByXPath(xPath).getAttribute("class");
    }

    protected fun assertTableRows(tableElementId: String, expectedRows: List<String>, orderMatters: Boolean) {
        if (orderMatters) {
            for ((index) in expectedRows.withIndex()) {
                try {
                    getWait().until {
                        val elements = findTableRowsById(tableElementId)
                        try {
                            elements!![index].text.contains(expectedRows[index])
                        } catch (e: StaleElementReferenceException) {
                            // ignore & retry
                        }
                    }
                } catch (e: TimeoutException) {
                    fail("Table expected value at $index to be ${expectedRows[index]} but was ${findTableRowsById(tableElementId)!![index].text}")
                }
            }
        } else {
            for (value in expectedRows) {
                try {
                    getWait().until(
                        ExpectedConditions.textToBePresentInElement(
                            driver.findElement(By.id(tableElementId)),
                            value
                        )
                    )
                } catch (e: TimeoutException) {
                    fail("Table did not contain expected value $value")
                }
            }
        }

        Assertions.assertEquals(expectedRows.size, findTableRowsById(tableElementId)!!.size)
    }

    protected fun assertTableEmptyById(tableElementId: String) {
        try {
            getWait().until {
                findTableRowsById(tableElementId)!!.size == 0
            }
        } catch (e: TimeoutException) {
            fail("Table size was supposed to be 0 but was: ${findTableRowsById(tableElementId)!!.size}")
        }
    }

    protected fun enterInputFieldById(id: String, value: String) {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
        driver.findElementById(id).click()
        driver.findElementById(id).clear()
        getWait().until({ getInputValueById(id) == "" })
        driver.findElementById(id).sendKeys(value)
        getWait().until({ getInputValueById(id) == value })
    }

    /**
     * When the input field has JS, enter the value one by one
     */
    protected fun enterInputFieldSlowlyById(id: String, value: String) {
        val characters = value.toCharArray()
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
        driver.findElementById(id).click()
        driver.findElementById(id).clear()
        getWait().until({ getInputValueById(id) == "" })

        for (character in characters) {
            driver.findElementById(id).sendKeys(character.toString())
        }
    }

    protected fun clearInputFieldById(id: String) {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
        driver.findElementById(id).clear()
        driver.findElementById(id).sendKeys(Keys.CONTROL, "a")
        driver.findElementById(id).sendKeys(Keys.BACK_SPACE)
        getWait().until({ getInputValueById(id) == "" })
    }

    protected fun enterInputFieldByXPath(xpath: String, value: String) {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
        driver.findElementByXPath(xpath).clear()
        driver.findElementByXPath(xpath).sendKeys(value)
        driver.findElementByXPath(xpath).sendKeys(Keys.RETURN)
        getWait().until({ getInputValueByXPath(xpath) == value })
    }

    protected fun enterDropdownFieldByXPath(xpath: String, indexToSelect: Int) {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
        val x = Select(driver.findElementByXPath(xpath))
        x.selectByIndex(indexToSelect)
    }

    protected fun getInputValueById(id: String): String {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(id)))
        return driver.findElementById(id).getAttribute("value")
    }

    protected fun getInputValueByXPath(xpath: String): String {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
        return driver.findElementByXPath(xpath).getAttribute("value")
    }

    protected fun getElementTextValueByXPath(xpath: String): String {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))
        return driver.findElementByXPath(xpath).text
    }

    protected fun clickElementWithWait(element: WebElement) {
        getWait().until({ (driver as JavascriptExecutor).executeScript("return document.readyState") == "complete" })
        element.click()
        getWait().until({ (driver as JavascriptExecutor).executeScript("return document.readyState") == "complete" })
    }

    protected fun assertInputValueWithWaitById(elementId: String, value: String) {
        getWait().until({ getInputValueById(elementId) == value })
    }

    protected fun assertInputValueWithWaitByXPath(xPath: String, value: String) {
        var actualResult = ""
        try {
            getWait().until {
                actualResult = getInputValueByXPath(xPath)
                getInputValueByXPath(xPath) == value
            }
        }catch (e: TimeoutException) {
            fail("Table expected value: $value but was: $actualResult")
        }
    }

    protected fun assertTextValueWithWaitByXPath(xPath: String, value: String) {
        var actualResult = ""
        try {
            getWait().until({
                actualResult = getElementTextValueByXPath(xPath)
                actualResult == value
            })
        } catch (e: TimeoutException) {
            fail("Table expected value: $value but was: $actualResult")
        }
    }

    protected fun getWait(): WebDriverWait {
        return WebDriverWait(driver, DEFAULT_WAIT)
    }

    protected fun findTableRowsById(tableId: String): MutableList<WebElement>? {
        // wait for the element to be present
        getWait().until(ExpectedConditions.visibilityOfElementLocated(By.id(tableId)))

        return driver.findElementById(tableId).findElements(By.cssSelector("tbody > tr"))
    }

    // clicks the body to remove focus on whatever
    protected fun resetFocus() {
        driver.findElementByTagName("body").click()
    }
}
