package com.selenium.practical.actions;

import com.selenium.practical.getpageobjects.GetPage;
import com.selenium.practical.utils.SeleniumWait;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import static com.selenium.practical.pages.Locators.*;

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
}
