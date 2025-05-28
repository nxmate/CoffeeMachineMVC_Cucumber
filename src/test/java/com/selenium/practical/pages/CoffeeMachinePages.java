package com.selenium.practical.pages;

import com.selenium.practical.actions.CoffeeMachineActions;
import com.selenium.practical.config.WebDriverFactory;
import com.selenium.practical.utils.ReportMsg;
import com.selenium.practical.utils.SeleniumWait;
import org.openqa.selenium.WebDriver;

public class CoffeeMachinePages {

    private final WebDriver driver;
    private final CoffeeMachineActions coffeeMachineActions;

    public CoffeeMachinePages(WebDriver driver, SeleniumWait wait) {
        this.driver = driver;
        this.coffeeMachineActions = new CoffeeMachineActions(driver, wait);
    }

    public void open(){
        driver.get("http://localhost:8080/");
        ReportMsg.log("Coffee Machine website has been opened.");
    }

    public void verifyHeader(String header) {
        coffeeMachineActions.verifyHeader(header);
    }

    public void verifyTitle(String status) {
        coffeeMachineActions.verifyTitle(status);
    }
}
