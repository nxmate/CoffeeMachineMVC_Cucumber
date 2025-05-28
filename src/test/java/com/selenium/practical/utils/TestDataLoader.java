package com.selenium.practical.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class TestDataLoader {

    private Map<String, Object> testData;

    // Singleton instance for single YAML loading
    private static TestDataLoader instance;

    private TestDataLoader() {
        loadTestData();
    }

    // Get singleton instance (lazy-loaded)
    public static TestDataLoader getInstance() {
        if (instance == null) {
            instance = new TestDataLoader();
        }
        return instance;
    }

    // Load YAML file into Map
    private void loadTestData() {
        Yaml yaml = new Yaml();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("testdata/TestData.yml")) {
            if (in == null) {
                throw new RuntimeException("Could not find TestData.yml in resources");
            }
            testData = yaml.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load TestData.yml", e);
        }
    }

    // Generic getter for nested map by key path (dot-separated)
    @SuppressWarnings("unchecked")
    public Object getValue(String keyPath) {
        String[] keys = keyPath.split("\\.");
        Map<String, Object> currentMap = testData;
        Object value = null;

        for (int i = 0; i < keys.length; i++) {
            if (currentMap == null) {
                return null;
            }
            value = currentMap.get(keys[i]);
            if (i < keys.length - 1) {
                currentMap = (Map<String, Object>) value;
            }
        }
        return value;
    }

    // Specific helper for coffeeMachine.statusTitle
    public String getCoffeeMachineStatusTitle() {
        Object value = getValue("coffeeMachine.statusTitle");
        return value != null ? value.toString() : null;
    }
}
