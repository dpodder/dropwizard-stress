package com.example.tools.stressclient;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Configurable properties for the stress client.
 */
@Getter
@Setter
public class Configuration
{
    /** Number of worker threads to use. */
    private int workerCount;

    /** Number of work sessions to enqueue. */
    private int sessionCount;

    /** Number of requests to make per session. */
    private int requestsPerSession;

    /** Probability of querying for a category, as opposed to an item. */
    private double catQueryProbability;

    /** Base URL for the remote REST endpoint to be stress-tested. */
    private String remoteService;

    /** Timeout for connecting to the remote service. */
    private double connectTimeout;

    /** Timeout when waiting for a response from the remote service. */
    private double readTimeout;

    /** Wait time between status updates. */
    private double updateInterval;

    /** Input data used to generate requests. */
    private List<CategoryData> categories;

    @Getter
    @Setter
    public static class CategoryData
    {
        /** Name of the category. */
        private String name;

        /** List of names of items in the category. */
        private List<String> items;
    }
}
