package com.selenium.practical.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.selenium.practical.utils.Timeouts.*;
import static java.time.Clock.systemDefaultZone;

public class SeleniumWait {
    WebDriver driver;
    WebDriverWait wait;
    int timeout;
    public final int BASE_TIMEOUT = 30;
    private AtomicInteger runningRequests;
    private ReportMsg Log;

    public SeleniumWait(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        this.timeout = (int) timeout.toSeconds();
        this.runningRequests = new AtomicInteger(0);
    }

    public void setNewTimeout(int newTimeOut) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(newTimeOut));
    }

    public void setBaseTimeout() {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(BASE_TIMEOUT));
    }

    public Alert getAlertWhenVisible() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public void waitForPageTitleToContain(String expectedPageTitle) {
        wait.until(ExpectedConditions.titleContains(expectedPageTitle));
    }

    public WebElement getWhenPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public List<WebElement> getAllWhenPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    public WebElement getWhenVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement getWhenVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    public boolean checkIfElementVisible(By locator) {
        boolean isVisible = false;

        try {
            isVisible = wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                    .isDisplayed();
        } catch (TimeoutException ex) {
            ReportMsg.info("Element isn't visible: " + locator);
        }
        return isVisible;
    }

    public boolean waitForElementToBeVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator))
                .isDisplayed();
    }

    public List<WebElement> waitForElementsToBeVisible(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public List<WebElement> waitForAllElementsToBeVisible(By element) {
        List<WebElement> result;

        try {
            result = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(element));
        } catch (StaleElementReferenceException e) {
            hardWait(3);
            result = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(element));
        }
        return result;
    }

    public boolean waitForElementToBeInvisible(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            return true;
        }
    }

    public WebElement getWhenClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public WebElement clickWhenReady(By locator) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            element.click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            element.click();
        }
        return element;
    }

    public void waitForFrameToBeAvailableAndSwitchToIt(By locator) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(locator));
    }

    public void waitForMsgToastToDisappear() {
        int i = 0;
        resetImplicitTimeout(1);
        try {
            while (driver.findElement(By.className("toast-message")).isDisplayed()
                    && i <= timeout) {
                hardWait(1);
                i++;
            }
        } catch (Exception ignored) {
        }
        resetImplicitTimeout(timeout);
    }

    public boolean waitForElementToDisappear(By element) {
        boolean isDisappeared = false;

        resetImplicitTimeout(5);

        for (int i = 0; i <= timeout; i++) {
            try {
                isDisappeared = driver.findElement(element).isDisplayed();
            } catch (Exception e) {
                break;
            }
            i++;
        }
        resetImplicitTimeout(timeout);
        return isDisappeared;
    }

    public void resetImplicitTimeout(int newTimeOut) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(newTimeOut));
    }

    public void waitForPageToLoadCompletely() {
        ExpectedCondition<Boolean> expectation = driver ->
        {
            assert driver != null;
            return ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState")
                    .toString().equals("complete");
        };
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

    public static ExpectedCondition<Boolean> pageLoadComplete() {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return (Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState.seeking === 'string_value' || ... ");
            }
        };
    }

    public void hardWait(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void waitForDomToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*")));
    }

    public int getTimeout() {
        return timeout;
    }

    public void waitForElementToLoad(By seleniumFindExpression, long seconds) {
        long now = new Date().getTime();
        long endTime = now + seconds * 10;
        while (now < endTime) {
            try {
                driver.findElement(seleniumFindExpression);
            } catch (NoSuchElementException e) {
                now = new Date().getTime();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
                continue;
            }
            break;
        }
        if (now > endTime) {
            throw new IllegalStateException(
                    "could not find element " + seleniumFindExpression.toString() + " within " + seconds + " seconds.");

        }

        resetImplicitTimeout(getTimeout());
    }

    public <V> V waitForEventToHappen(Function<? super WebDriver, V> event) {
        return wait.until(event);
    }


    public static boolean waitForSolidStreak(Supplier<Boolean> condition,
                                             Duration streakLengthInMs, Duration timeoutInSec, int intervalInMs) {

        Clock clock = systemDefaultZone();
        Instant end = clock.instant().plus(timeoutInSec);
        Instant endOfStreak = clock.instant().plus(streakLengthInMs);

        do {
            try {
                if (!condition.get()) {
                    endOfStreak = clock.instant().plus(streakLengthInMs);
                }
                waitFor(intervalInMs);
            } catch (Exception ex) {
                waitFor(intervalInMs);
            }
        } while (clock.instant().isBefore(end) && clock.instant().isBefore(endOfStreak));

        if (endOfStreak.isAfter(end)) {
            System.out.printf("Wait for %s timed out%n", condition.getClass().getName());
            return false;
        }
        return true;
    }

    public static void waitFor(long milliSecToWait) {
        try {
            Thread.sleep(milliSecToWait);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static <T> boolean waitFor(T item, Duration timeout, int pollingIntervalInMs, Predicate<T> condition) {
        Clock clock = systemDefaultZone();

        Instant end = clock.instant().plus(timeout);
        boolean isReady = false;
        do {
            try {
                isReady = condition.test(item);
                Thread.sleep(pollingIntervalInMs);
            } catch (Exception ex) {
                try {
                    Thread.sleep(pollingIntervalInMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (clock.instant().compareTo(end) <= 0 && !isReady);

        return isReady;
    }

    public static void waitFor(Supplier<Boolean> condition, Duration timeout, Duration interval) {
        Clock clock = systemDefaultZone();
        Instant end = clock.instant().plus(timeout);

        while (clock.instant().isBefore(end)) {
            try {
                if (condition.get()) {
                    return;
                }
                waitFor(interval.toMillis());
            } catch (Exception ex) {
                waitFor(interval.toMillis());
            }
        }
        ReportMsg.log("Wait for {} timed out", condition.getClass().getName());
    }

    public void waitForSolidNetworkSilence() {
        waitForSolidNetworkSilence(MINIMAL_TIMEOUT);
    }

    public void waitForSolidNetworkSilence(Duration duration) {
        if (!waitForSolidStreak(this::isNetworkActivityStopped, DEFAULT_STREAK, duration, MINIMAL_INTERVAL)) {
            runningRequests.set(0);
            ReportMsg.log("Running requests set to 0 after the timeout for " + SeleniumWait.class.getSimpleName());
        }
    }

    public void waitForNetworkSilence() {
        waitFor(this::isNetworkActivityStopped, MINIMAL_TIMEOUT, Duration.ofMillis(DEFAULT_INTERVAL));
    }

    private boolean isNetworkActivityStopped() {
        final Set<String> trackedTypes = Set.of(
                "Document", "Stylesheet", "Image", "Media", "Font",
                "Script", "XHR", "Fetch", "EventSource", "WebSocket",
                "Manifest", "Other"
        );
        final boolean enableVerboseLogging = true;

        java.util.function.Function<String, String> extractNameFromUrl = (url) -> {
            if (url == null || url.isEmpty()) return "unknown";
            try {
                java.net.URL netUrl = new java.net.URL(url);
                String path = netUrl.getPath();
                if (path == null || path.isEmpty() || path.equals("/")) {
                    return netUrl.getHost();
                }
                String[] segments = path.split("/");
                return segments.length > 0 ? segments[segments.length - 1] : path;
            } catch (Exception e) {
                return "unknown";
            }
        };

        LogEntries entries = driver.manage().logs().get(LogType.PERFORMANCE);
        boolean found = false;

        Map<String, JsonObject> requestsMap = new HashMap<>();
        Map<String, Integer> responseStatusMap = new HashMap<>();

        for (LogEntry entry : entries) {
            try {
                JsonObject json = JsonParser.parseString(entry.getMessage()).getAsJsonObject();
                JsonObject message = json.getAsJsonObject("message");
                if (message == null) continue;

                String method = message.get("method").getAsString();

                if ("Network.requestWillBeSent".equals(method)) {
                    JsonObject params = message.getAsJsonObject("params");
                    if (params != null && params.has("type")) {
                        String type = params.get("type").getAsString();
                        if (trackedTypes.contains(type)) {
                            found = true;

                            JsonObject request = params.getAsJsonObject("request");
                            String requestId = params.has("requestId") ? params.get("requestId").getAsString() : "N/A";
                            String url = request != null && request.has("url") ? request.get("url").getAsString() : "unknown";
                            String requestMethod = request != null && request.has("method") ? request.get("method").getAsString() : "unknown";

                            requestsMap.put(requestId, request);

                            if (enableVerboseLogging) {
                                String name = extractNameFromUrl.apply(url);
                                String logMessage = String.format("Request started: %s, %s, %s, %s",
                                        name, requestMethod, type, url);
//                                 Remove comment to have logs for investigation
//                                 ReportMsg.log(logMessage);
                            }
                        }
                    }
                }

                if ("Network.responseReceived".equals(method)) {
                    JsonObject params = message.getAsJsonObject("params");
                    if (params != null && params.has("type") && params.has("response")) {
                        String type = params.get("type").getAsString();
                        if (trackedTypes.contains(type)) {
                            JsonObject response = params.getAsJsonObject("response");
                            String requestId = params.has("requestId") ? params.get("requestId").getAsString() : "N/A";
                            int status = response.has("status") ? response.get("status").getAsInt() : -1;

                            responseStatusMap.put(requestId, status);

                            if (enableVerboseLogging) {
                                String url = response.has("url") ? response.get("url").getAsString() : "unknown";
                                String name = extractNameFromUrl.apply(url);
                                String logMessage = String.format("Response received: %s, %d, %s, %s",
                                        name, status, type, url);
//                                 Remove comment to have logs for investigation
//                                 ReportMsg.log(logMessage);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                if (enableVerboseLogging) {
                    ReportMsg.log("Malformed log skipped: " + e.getMessage());
                }
            }
        }
        return !found;
    }

    /*
     * Waiting the scripts for specified period of time
     */
    public void waitForSpecifiedTime(int totalSeconds) {
        int interval = 10;
        int waited = 0;

        while (waited < totalSeconds) {
            try {
                Thread.sleep(Math.min(interval, totalSeconds - waited) * 1000L);
                driver.getTitle();
                waited += interval;
            } catch (Exception ignored) {
            }
        }
        ReportMsg.log("User wait for " + totalSeconds + " seconds");
    }
}
