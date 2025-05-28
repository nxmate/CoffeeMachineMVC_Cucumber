package com.selenium.practical.getpageobjects;

import com.selenium.practical.utils.ReportMsg;
import com.selenium.practical.utils.SeleniumWait;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;

import java.time.Duration;
import java.util.Objects;

import static com.selenium.practical.utils.ReportMsg.log;
import static java.lang.System.getProperty;
import static org.testng.Assert.fail;

public class GetPage {
    protected SeleniumWait wait;
    private final WebDriver driver;
    protected final int AJAX_WAIT = 5;
    public String pageName;

    public GetPage(WebDriver driver, SeleniumWait wait, String pageName) {
        this.driver = driver;
        this.wait = wait;
        this.pageName = pageName;
    }

    public int getConfigTimeOut() {
        return Integer.parseInt(Objects.requireNonNull(getProperty("timeout")));
    }

    public void selectDropDownText(WebElement e, String value) {
        Select dropdown = new Select(e);
        dropdown.selectByVisibleText(value);
        Reporter.log(value + " is selected");
        // waitTOSync();
    }

    protected By getLocator(By elementToken) {
        return getLocator(elementToken, "");
    }

    protected By getLocator(By elementToken, String replacement) {
        if (!replacement.isEmpty()) {
            String loc = elementToken.toString().replaceAll("'", "\"");
            String type = loc.split(":", 2)[0].split(",")[0].split("\\.")[1];
            String variable = loc.split(":", 2)[1].replaceAll("\\$\\{.+?}", replacement);
            return getBy(type, variable);
        } else {
            return elementToken;
        }
    }

    protected By getLocator(By elementToken, String replacement1, String replacement2) {
        if (!replacement1.isEmpty()) {
            String loc = elementToken.toString().replaceAll("'", "\\\"");
            String type = loc.split(":", 2)[0].split(",")[0].split("\\.")[1];
            String variable = loc.split(":", 2)[1].replaceAll("\\$\\{.+?\\}", replacement1);
            if (!replacement2.isEmpty()) {
                variable = variable.replaceAll("\\%\\{.+?\\}", replacement2);
            }
            return getBy(type, variable);
        }
        if (!replacement2.isEmpty()) {
            String loc = StringUtils.replace(elementToken.toString(), "$", replacement1);
            String type = loc.split(":")[0].split(",")[0];
            String variable = loc.split(":")[1];
            return getBy(type, variable);
        } else {
            return elementToken;
        }
    }

    private By getBy(String locatorType, String locatorValue) {
        switch (Locators.valueOf(locatorType)) {
            case id:
                return By.id(locatorValue);
            case xpath:
                return By.xpath(locatorValue);
            case css:
            case cssSelector:
                return By.cssSelector(locatorValue);
            case name:
                return By.name(locatorValue);
            case classname:
                return By.className(locatorValue);
            case linktext:
                return By.linkText(locatorValue);
            default:
                return By.id(locatorValue);
        }
    }

    public boolean checkIfElementVisible(By elementToken) {
        return checkIfElementVisible(elementToken, "");
    }

    public boolean checkIfElementVisible(By elementToken, String replacement) {
        boolean flag = false;

        try {
            flag = wait.waitForElementToBeVisible(getLocator(elementToken, replacement));
        } catch (Exception ignored) {
        } finally {
            wait.resetImplicitTimeout(getConfigTimeOut());
            wait.setBaseTimeout();
        }
        return flag;
    }

    public boolean isElementVisibleAfterCustomTimeout(By elementToken, String replacement, int customTimeout) {
        // resetting the current implicit timeout makes the following snippet to work as it should
        // see for details: https://stackoverflow.com/a/33647864
        boolean flag;
        By searchedItem = getLocator(elementToken, replacement);
        FluentWait<WebDriver> waiter = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(customTimeout))
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(customTimeout));

        try {
            flag = waiter.until(driver -> driver.findElement(searchedItem))
                    .isDisplayed();
        } catch (StaleElementReferenceException stale_ex) {
            wait.waitForDomToLoad();
            flag = waiter.until(driver -> driver.findElement(searchedItem))
                    .isDisplayed();
        } catch (TimeoutException to_ex) {
            flag = false;
            log("Element isn't present after waiting for the custom amount of time: " + customTimeout + "....." + searchedItem);
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(getConfigTimeOut()));

        return flag;
    }

    protected boolean checkIfElementVisible(WebElement element) {
        if (element != null) {
            return element.isDisplayed();
        }
        return false;
    }


    protected void waitForElementBecomeVisible(By element) {
        for (int second = 0; ; second++) {
            if (second >= AJAX_WAIT) {
                Reporter.log("element not present");
                break;
            } else {
                wait.resetImplicitTimeout(3);
                try {
                    getElementWhenVisible(element);
                    wait.resetImplicitTimeout(AJAX_WAIT);
                    Reporter.log(element + " is present");
                    break;
                } catch (Exception ee) {
                    wait.hardWait(2);
                }
            }
        }
    }

    protected WebElement getElementWhenVisible(By elementToken, String replacement) {
        WebElement foundElement = null;

        try {
            foundElement = wait.getWhenVisible(getLocator(elementToken, replacement));
        } catch (NoSuchElementException excp) {
            fail(ReportMsg.log("[ASSERT FAILED]: Element " + elementToken + " not found on the webPage !!!"));
        } catch (NullPointerException npe) {
            fail("[UNHANDLED EXCEPTION]: " + npe.getLocalizedMessage());
        }
        return foundElement;
    }

    protected WebElement getElementWhenVisible(By elementToken) {
        return getElementWhenVisible(elementToken, "");
    }

    protected WebElement getElementWhenVisible(By elementToken, String replacement1, String replacement2) {
        return getElementWhenVisible(getLocator(elementToken, replacement1, replacement2));
    }

    protected WebElement getElementWhenVisible(WebElement elementToken) {
        WebElement foundElement = null;

        try {
            foundElement = wait.getWhenVisible(elementToken);
        } catch (NoSuchElementException excp) {
            fail(ReportMsg.log("[ASSERT FAILED]: Element " + elementToken + " not found on the webPage !!!"));
        } catch (NullPointerException npe) {
            fail("[UNHANDLED EXCEPTION]: " + npe.getLocalizedMessage());
        }
        return foundElement;
    }
}
