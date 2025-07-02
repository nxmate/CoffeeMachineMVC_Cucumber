package com.selenium.practical.driver;

import com.selenium.practical.utils.NetworkWaiter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;

public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<NetworkWaiter> networkWaiter = new ThreadLocal<>();

    public static void createDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeDriver chromeDriver = new ChromeDriver();

        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();

        NetworkWaiter waiter = new NetworkWaiter(devTools);
        waiter.startTracking();
        waiter.enableDetailedLogging();
        driver.set(chromeDriver);
        networkWaiter.set(waiter);
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static NetworkWaiter getNetworkWaiter() {
        return networkWaiter.get();
    }

    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
            networkWaiter.remove();
        }
    }
}