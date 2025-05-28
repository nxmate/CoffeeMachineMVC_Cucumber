package com.selenium.practical.utils;

import java.time.Duration;

public abstract class Timeouts {

    //intervals in ms
    public static final int MINIMAL_INTERVAL = 100;
    public static final int SHORT_INTERVAL = 200;
    public static final int DEFAULT_INTERVAL = 500;
    public static final int LONG_INTERVAL = 5000;
    public static final int INTERVAL_FOR_CHECKING_PERMISSION_UPDATE = 120;

    //timeouts in sec
    public static final Duration MINIMAL_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration SHORT_TIMEOUT = Duration.ofSeconds(30);
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    public static final Duration LONG_TIMEOUT = Duration.ofSeconds(120);
    public static final Duration MAXIMAL_TIMEOUT = Duration.ofSeconds(650);
    public static final Duration TIMEOUT_FOR_CHECKING_PERMISSION_UPDATE = Duration.ofSeconds(28800);
    //special cases
    public static final Duration DB_UPDATE_TIMEOUT = Duration.ofSeconds(2000);

    //Server maintenance cases
    public static final Duration MAINTENANCE_ERROR_TIMEOUT = Duration.ofMinutes(30);
    public static final int MAINTENANCE_ERROR_INTERVAL = 30000;

    //time periods for waitForSolidStreak
    public static final Duration DEFAULT_STREAK = Duration.ofSeconds(1);
}