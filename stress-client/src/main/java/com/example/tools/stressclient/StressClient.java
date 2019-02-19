package com.example.tools.stressclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.example.tools.stressclient.Configuration.CategoryData;
import com.example.tools.stressclient.resources.CategoryClient;
import com.example.tools.stressclient.resources.ChecksumContainer;
import com.example.tools.stressclient.resources.ItemClient;
import com.example.tools.stressclient.resources.ResourceMismatchException;
import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.ProcessingException;
import lombok.RequiredArgsConstructor;

/**
 * The main application class.
 */
public class StressClient
{
    private static final String TOTAL_REQUESTS = "Total Requests";
    private static final String COMPLETED_REQUESTS_COUNT = "Completed Requests";
    private static final String REPRO_WRONG_OBJECT = "Repro - Wrong Object";
    private static final String REPRO_4XX_RESPOSNE = "Repro - 4xx Response";
    private static final String OTHER_ERROR_COUNT = "Other Errors";
    private static final String PASSING_COUNT = "Successful Requests";

    /**
     * The main entry point.
     *
     * @param args
     *          the command line arguments.
     * @throws Exception
     *          if anything fails during execution.
     */
    public static void main(String[] args)
    throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("USAGE: stress-client <config.yaml>");
            System.exit(1);
        }
        File configFile = new File(args[0]);
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        Configuration config = yamlMapper.readValue(configFile, Configuration.class);

        RemoteClient client = new RemoteClient(config);
        CategoryClient categoryClient = new CategoryClient(client);
        ItemClient itemClient = new ItemClient(client);

        long totalRequests = config.getSessionCount() * (long) config.getRequestsPerSession();
        long updateIntervalMs = (long) (config.getUpdateInterval() * 1000);

        Map<String, AtomicLong> statistics = new HashMap<>();
        statistics.put(TOTAL_REQUESTS, new AtomicLong(totalRequests));
        statistics.put(COMPLETED_REQUESTS_COUNT, new AtomicLong());
        statistics.put(REPRO_WRONG_OBJECT, new AtomicLong());
        statistics.put(REPRO_4XX_RESPOSNE, new AtomicLong());
        statistics.put(OTHER_ERROR_COUNT, new AtomicLong());
        statistics.put(PASSING_COUNT, new AtomicLong());

        log("Pre-loading all data for the result cache ...");
        ConcurrentMap<String, ChecksumContainer> resultCache = new ConcurrentHashMap<>();
        // Pre-load the cache
        for (CategoryData catConfig : config.getCategories())
        {
            JsonNode category = categoryClient.getCategory(
                RequestIdFactory.nextId("BASE-"), catConfig.getName());
            ChecksumContainer categoryContainer = new ChecksumContainer(category);
            resultCache.put(catConfig.getName(), categoryContainer);
            for (String itemName : catConfig.getItems())
            {
                JsonNode item = itemClient.getItem(
                    RequestIdFactory.nextId("BASE-"), catConfig.getName(), itemName);
                ChecksumContainer itemContainer = new ChecksumContainer(item);
                resultCache.put(itemName, itemContainer);
            }
        }

        log("Starting stress test ...");
        ExecutorService svc = Executors.newFixedThreadPool(config.getWorkerCount());
        for (int i = 0; i < config.getSessionCount(); i++)
        {
            svc.execute(
                new Task(
                    config,
                    categoryClient,
                    itemClient,
                    resultCache,
                    statistics,
                    Thread.currentThread()));
        }
        svc.shutdown();
        boolean done = false;
        printStatus(statistics);
        while (!done)
        {
            try
            {
                done = svc.awaitTermination(updateIntervalMs, TimeUnit.MILLISECONDS);
                printStatus(statistics);
            }
            catch (InterruptedException ex)
            {
                System.out.println();
                log("Test interrupted, shutting down ...");
                svc.shutdownNow();
            }
        }
        System.out.println();
        log("Done!");
    }

    private static void log(String message)
    {
        String timestamp = ZonedDateTime.now(ZoneOffset.UTC)
            .truncatedTo(ChronoUnit.SECONDS)
            .format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println("\r[" + timestamp + "] " + message);
    }

    private static void printStatus(Map<String, AtomicLong> statistics)
    {
        long completed = statistics.get(COMPLETED_REQUESTS_COUNT).get();
        long total = statistics.get(TOTAL_REQUESTS).get();
        long passing = statistics.get(PASSING_COUNT).get();
        long wrongObject = statistics.get(REPRO_WRONG_OBJECT).get();
        long error4xx = statistics.get(REPRO_4XX_RESPOSNE).get();
        System.out.printf(
            "\rPercent complete: %.2f%% (%d OK, %d wrong-object, %d error-4xx, %d total) ... ",
            (100. * completed / total), passing, wrongObject, error4xx, completed);
    }

    @RequiredArgsConstructor
    private static class Task implements Runnable
    {
        private final Configuration config;
        private final CategoryClient categoryClient;
        private final ItemClient itemClient;
        private final ConcurrentMap<String, ChecksumContainer> resultCache;
        private final Map<String, AtomicLong> statistics;
        private final Thread mainThread;

        @Override
        public void run()
        {
            final List<CategoryData> categories = config.getCategories();
            try
            {
                for (int round = 0; round < config.getRequestsPerSession(); round++)
                {
                    if (Thread.interrupted())
                    {
                        throw new InterruptedException();
                    }
                    String reqId = null;
                    String key;
                    JsonNode result;
                    try
                    {
                        if (ThreadLocalRandom.current().nextDouble() <
                            config.getCatQueryProbability())
                        {
                            key = categories
                                .get(ThreadLocalRandom.current().nextInt(categories.size()))
                                .getName();
                            reqId = RequestIdFactory.nextId("CAT-");
                            result = categoryClient.getCategory(reqId, key);
                        }
                        else
                        {
                            CategoryData category = categories
                                .get(ThreadLocalRandom.current().nextInt(categories.size()));
                            key = category.getItems()
                                .get(ThreadLocalRandom.current().nextInt(category.getItems().size()));
                            reqId = RequestIdFactory.nextId("ITEM-");
                            result = itemClient.getItem(reqId, category.getName(), key);
                        }
                        ChecksumContainer current = new ChecksumContainer(result);
                        ChecksumContainer previous = resultCache.computeIfAbsent(
                            key, name -> current);
                        if (previous.getCrc32() != current.getCrc32())
                        {
                            throw new ResourceMismatchException(
                                "CRC mismatch!",
                                previous.getObject().toString(),
                                result.toString());
                        }
                        statistics.get(PASSING_COUNT).incrementAndGet();
                    }
                    catch (ResourceMismatchException ex)
                    {
                        statistics.get(REPRO_WRONG_OBJECT).incrementAndGet();
                        log("REPRO! Req: [" + reqId + "] " + ex.getMessage());
                        printStatus(statistics);
                        // mainThread.interrupt();
                        // return;
                    }
                    catch (IOException ex)
                    {
                        statistics.get(OTHER_ERROR_COUNT).incrementAndGet();
                        log("IOE    Req: [" + reqId + "] " + ex.getMessage());
                        ex.printStackTrace();
                        printStatus(statistics);
                        // Swallow the exception; we want to continue
                    }
                    catch (ResponseFailedException ex)
                    {
                        if (ex.getStatusCode() >= 400 && ex.getStatusCode() < 500)
                        {
                            statistics.get(REPRO_4XX_RESPOSNE).incrementAndGet();
                        }
                        else
                        {
                            statistics.get(OTHER_ERROR_COUNT).incrementAndGet();
                        }
                        log("HTTP   Req: [" + reqId + "] " + ex.getMessage()
                            + "\n  URL: " + ex.getUrl()
                            + "\n  Response: " + ex.getResponse());
                        printStatus(statistics);
                        // Swallow the exception; we want to continue
                    }
                    catch (ProcessingException ex)
                    {
                        statistics.get(OTHER_ERROR_COUNT).incrementAndGet();
                        log("TOUT   Req: [" + reqId + "] " + ex.getMessage());
                        printStatus(statistics);
                        // Swallow the exception; we want to continue
                    }
                    statistics.get(COMPLETED_REQUESTS_COUNT).incrementAndGet();
                }
            }
            catch (InterruptedException ignored)
            {
                // Nothing to do; just halt
            }
        }
    }
}
