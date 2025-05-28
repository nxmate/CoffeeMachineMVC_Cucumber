package com.selenium.practical.pages;

import org.openqa.selenium.By;

public class Selectors {

    public static final By HEADER = By.xpath("//h1[text()='Coffee Machine']");
    public static final By STATUS = By.xpath("//pre[@id='status']");
    public static final By HEADER2 = By.xpath("//h3[text()='Buy Coffee']");
    public static final By LABEL_TEXT = By.xpath("//label[contains(normalize-space(.), 'Type:')]");
    public static final By LABEL = By.xpath("//select[@id='type']");
//    public static final By OPTION_VALE_PARAMETRIZED = By.xpath("//select[@id='type']/option[@value='{coffeetype}']");
    //Dynamic selector for coffee types
    public static By getCoffeeTypeOption(String coffeeType) {
        return By.xpath(String.format("//select[@id='type']/option[@value='%s']", coffeeType.toLowerCase()));
    }

    public static final By OPTION_VALUE_ESPRESSO = By.xpath("//select[@id='type']/option[@value='espresso']");
    public static final By OPTION_VALUE_LATTE = By.xpath("//select[@id='type']/option[@value='latte']");
    public static final By OPTION_VALUE_CAPPUCCINO = By.xpath("//select[@id='type']/option[@value='cappuccino']");
    public static final By CUPS_INPUT_FIELD = By.xpath("//input[@id='cups']");
    public static final By BUY_BUTTON = By.xpath("//button[@onclick='buyCoffee()']");
    public static final By FILL_BUTTON = By.xpath("//button[text()='Fill']");
    public static final By TAKE_MONEY_BUTTON = By.xpath("//button[text()='Take Money']");
    public static final By HEADER3 = By.xpath("//h3[text()='Other Actions']");
}
