package com.selenium.practical.runners;

import com.selenium.practical.driver.DriverManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

@CucumberOptions(
        // Path to your feature files
        features = "src/test/resources/features",

        // Package where your step definitions and hooks are
        glue = "com.selenium.practical",

        // The tags to run
        tags = "@smoke",

        // Reporting plugins
        plugin = {"pretty", "json:target/cucumber-report.json"}
)
public class TestRunner extends AbstractTestNGCucumberTests {

    @BeforeClass
    public static void setup() {
        DriverManager.createDriver();
    }

    @AfterClass
    public static void teardown() {
        DriverManager.quitDriver();
    }
}