package com.selenium.practical.utils;

import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v138.network.Network;
import org.openqa.selenium.devtools.v138.network.model.Request;
import org.openqa.selenium.devtools.v138.network.model.Headers;
import org.openqa.selenium.devtools.v138.network.model.Response;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility for tracking and waiting on network requests using Chrome DevTools Protocol (CDP).
 */
public class NetworkWaiter {

    private final DevTools devTools;
    private final Map<String, Request> activeRequests = new ConcurrentHashMap<>();
    private final Set<String> completedRequests = ConcurrentHashMap.newKeySet();
    private final Set<String> failedRequests = ConcurrentHashMap.newKeySet();
    private final Map<String, Headers> requestHeaders = new ConcurrentHashMap<>();
    private final Map<String, Response> responseInfo = new ConcurrentHashMap<>();
    private final Map<String, String> responseBodies = new ConcurrentHashMap<>();
    private boolean isDetailedLoggingEnabled = false;

    public NetworkWaiter(DevTools devTools) {
        this.devTools = devTools;
    }

    public void startTracking() {
        devTools.send(Network.enable(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.of(false)
        ));

        devTools.addListener(Network.requestWillBeSent(), event -> {
            String id = event.getRequestId().toString();
            if (isDetailedLoggingEnabled) {
                ReportMsg.log("Request will be sent. ID: " + id + " URL: " + event.getRequest().getUrl());
            }
            if (!completedRequests.contains(id)) {
                activeRequests.put(id, event.getRequest());
                requestHeaders.put(id, event.getRequest().getHeaders());
            }
        });

        devTools.addListener(Network.responseReceived(), event -> {
            String id = event.getRequestId().toString();
            Response response = event.getResponse();
            if (isDetailedLoggingEnabled) {
                ReportMsg.log("Response received. ID: " + id + " Status: " + response.getStatus());
            }
            responseInfo.put(id, response);

            try {
                String responseBody = devTools.send(Network.getResponseBody(event.getRequestId())).getBody();
                responseBodies.put(id, responseBody);
                if (isDetailedLoggingEnabled) {
                    ReportMsg.log("Response body captured for ID: " + id);
                    logRequestDetails(id);
                    logResponseDetails(id);
                }
            } catch (Exception e) {
                if (isDetailedLoggingEnabled) {
                    ReportMsg.log("Could not retrieve response body for ID: " + id + ". It may be empty or unavailable.");
                }
            }
        });

        devTools.addListener(Network.loadingFinished(), event -> {
            String id = event.getRequestId().toString();
            if (isDetailedLoggingEnabled) {
                ReportMsg.log("Request finished. ID: " + id);
            }
            if (!completedRequests.contains(id)) {
                completedRequests.add(id);
                activeRequests.remove(id);
            }
        });

        devTools.addListener(Network.loadingFailed(), event -> {
            String id = event.getRequestId().toString();
            if (isDetailedLoggingEnabled) {
                ReportMsg.log("Request failed. ID: " + id + " Reason: " + event.getErrorText());
            }
            if (activeRequests.containsKey(id)) {
                failedRequests.add(id);
                activeRequests.remove(id);
            }
        });
    }

    public void reset() {
        ReportMsg.log("Resetting all tracked network data.");
        activeRequests.clear();
        completedRequests.clear();
        failedRequests.clear();
        requestHeaders.clear();
        responseInfo.clear();
        responseBodies.clear();
        ReportMsg.log("NETWORK: [RESET] Cleared all tracked requests.");
    }

    public void waitForAllNetworkRequestsToFinish(Duration timeout) {
        if (activeRequests.isEmpty()) {
            return;
        }

        Map<String, Request> requestsBeingMonitored = new ConcurrentHashMap<>(activeRequests);

        ReportMsg.log("NETWORK: [WAITING] Waiting for " + requestsBeingMonitored.size() + " active network requests to finish...");
        requestsBeingMonitored.forEach((id, request) ->
                ReportMsg.log("  - " + "ID: " + id + " | " + request.getMethod() + " " + request.getUrl())
        );

        Instant startTime = Instant.now();
        while (!activeRequests.isEmpty()) {
            if (Duration.between(startTime, Instant.now()).compareTo(timeout) > 0) {
                ReportMsg.log("WARNING: Not all network requests finished in time.");
                ReportMsg.log("ACTIVE REQUESTS AT TIMEOUT:");
                activeRequests.forEach((id, request) -> {
                    ReportMsg.log("  - " + "ID: " + id + " | " + request.getMethod() + " " + request.getUrl());
//                    logRequestDetails(id);
//                    logResponseDetails(id);
                });
                return;
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ReportMsg.log("WARNING: Network wait interrupted.");
                return;
            }
        }

        boolean hasFailures = false;
        for (String id : requestsBeingMonitored.keySet()) {
            if (failedRequests.contains(id)) {
                if (!hasFailures) {
                    ReportMsg.log("NETWORK: [WARNING] Some monitored requests failed:");
                    hasFailures = true;
                }
                Request failedRequest = requestsBeingMonitored.get(id);
                ReportMsg.log("  - " + failedRequest.getMethod() + " " + failedRequest.getUrl());
            }
        }

        ReportMsg.log("NETWORK: [CONTINUING] All monitored network requests finished.");
    }

    private void logRequestDetails(String id) {
        if (requestHeaders.containsKey(id)) {
            ReportMsg.log("    Request Headers: " + requestHeaders.get(id).toJson());
        }
    }

    private void logResponseDetails(String id) {
        if (responseInfo.containsKey(id)) {
            Response response = responseInfo.get(id);
            ReportMsg.log("    Response Status: " + response.getStatus() + " " + response.getStatusText());
            ReportMsg.log("    Response Headers: " + response.getHeaders().toJson());
        }
        if (responseBodies.containsKey(id)) {
            String body = responseBodies.get(id);
            ReportMsg.log("    Response Body: " + (body.length() > 500 ? body.substring(0, 500) + "..." : body));
        }
    }

    //    call networkWaiter.enableDetailedLogging(); to enable logging
    public void enableDetailedLogging() {
        this.isDetailedLoggingEnabled = true;
        ReportMsg.log("Detailed network request logging has been ENABLED.");
    }

    //    call networkWaiter.disableDetailedLogging(); to disable logging
    public void disableDetailedLogging() {
        this.isDetailedLoggingEnabled = false;
        ReportMsg.log("Detailed network request logging has been DISABLED.");
    }

}