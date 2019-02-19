package com.example.tools.stressclient;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Static class for generating random, unique request IDs.
 */
public class RequestIdFactory
{
    /**
     * Returns a new request ID with no prefix.
     *
     * @return
     *          a new request ID.
     */
    public static String nextId()
    {
        return nextId("");
    }

    /**
     * Returns a new request ID using the supplied prefix.
     *
     * @param prefix
     *          a prefix to appear in the generated request ID.
     * @return
     *          a new request ID.
     */
    public static String nextId(String prefix)
    {
        long randomLong = ThreadLocalRandom.current().nextLong();
        return prefix + Long.toHexString(randomLong);
    }
}
