package com.selenium.practical.stepdefinitions;

import com.selenium.practical.config.WebDriverFactory;
import com.selenium.practical.pages.CoffeeMachinePages;
import com.selenium.practical.utils.ReportMsg;
import com.selenium.practical.utils.SeleniumWait;
import com.selenium.practical.utils.TestDataLoader;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;

public class CoffeeMachineSteps {
    protected SeleniumWait wait;
    private WebDriver driver;
    private CoffeeMachinePages page;

    @Before
    public void setup() {
        driver = WebDriverFactory.createDriver();
        wait = new SeleniumWait(driver, 30);
        page = new CoffeeMachinePages(driver, wait);
    }

    @Given("I open the coffee machine website")
    public void openWebSite() {
        page.open();
    }

    @When("I verify the header is (.*)$")
    public void verifyHeader(String header) {
        page.verifyHeader(header);
    }

    @Then("I verify coffee machine status title")
    public void verifyTitle() {
        String expectedStatusTitle = TestDataLoader.getInstance().getCoffeeMachineStatusTitle();
        page.verifyTitle(expectedStatusTitle);
    }

    @Then("I verify that the second header is (.*)$")
    public void verifyHeader2(String header) {
        page.verifyHeader2(header);
    }

    @Then("I verify label is present")
    public void verifyLabelIsPresent() {
        page.verifyLabelIsPresent();
    }

    @Then("I verify label text is (.*)$")
    public void verifyLabelText(String labelText) {
        page.verifyLabelText(labelText);
    }

    @Then("I select (.*) as coffee type$")
    public void selectCoffeeType(String coffeeType) {
        page.selectCoffeeType(coffeeType);
    }

    @And("I wait for solid network silence")
    public void waitForSolidNetworkSilence() {
        wait.waitForSolidNetworkSilence();
        ReportMsg.log("User wait for solid network silence.");
    }

    @When("^I wait for (\\d+) seconds$")
    public void waitForSpecifiedTime(int sec) {
        wait.waitForSpecifiedTime(sec);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
