package com.selenium.practical.actions;

import com.selenium.practical.getpageobjects.GetPage;
import com.selenium.practical.pages.Selectors;
import com.selenium.practical.utils.SeleniumWait;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import static com.selenium.practical.pages.Selectors.*;

public class CoffeeMachineActions extends GetPage {

    public CoffeeMachineActions(WebDriver driver, SeleniumWait wait) {
        super(driver, wait, "CoffeeMachineActions");
    }

    public void verifyHeader(String header) {
        String actualHeader = getElementWhenVisible(HEADER, "").getText();
        System.out.println(actualHeader);
        Assert.assertEquals(actualHeader, header, "[Assertion FAIL]: headers are not equals.");
    }

    public void verifyTitle(String status) {
        String actualTitle = getElementWhenVisible(STATUS).getText();
        String normalizedExpectedTitle = status.replace("\r\n", "\n").trim().replaceAll("\\s+", " ");
        String normalizedActualTitle = actualTitle.replace("\r\n", "\n").trim().replaceAll("\\s+", " ");
        Assert.assertEquals(normalizedActualTitle, normalizedExpectedTitle,
                "[Assertion FAIL]: titles are not equals.");
    }

    public void verifyHeader2(String header) {
        String actualHeader = getElementWhenVisible(HEADER2).getText();
        boolean isEquals = actualHeader.equals(header);
        Assert.assertTrue(isEquals, "[Assertion FAIL]: Second header is not equal to the given string.");
    }

    public void verifyLabelIsPresent() {
        Assert.assertTrue(getElementWhenVisible(LABEL).isDisplayed(),
                "[Assertion PASS]: Label is present on the page.");
    }

    public void verifyLabelText(String labelText) {
        String actualText = getElementWhenVisible(LABEL_TEXT).getText().trim();
//        boolean isEquals = actualText.equals(expectedText);
        Assert.assertTrue(actualText.startsWith(labelText), "[Assertion FAIL]: Label text is not equal to the given string.");
    }

    public void selectCoffeeType(String coffeeType) {
        getElementWhenVisible(OPTION_VALUE_ESPRESSO).click();
        getElementWhenVisible(Selectors.getCoffeeTypeOption(coffeeType)).click();
//        getElementWhenVisible(OPTION_VALE_PARAMETRIZED, coffeeType).click();
    }
}
