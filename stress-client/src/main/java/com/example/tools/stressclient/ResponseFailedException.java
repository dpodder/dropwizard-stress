package com.example.tools.stressclient;

import lombok.Getter;

/**
 * Exception thrown when an unexpected HTTP response is received.
 */
public class ResponseFailedException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * The HTTP status code from the server.
     */
    @Getter
    private final int statusCode;

    /**
     * The URL of the request.
     */
    @Getter
    private final String url;

    /**
     * The response body from the server.
     */
    @Getter
    private final String response;

    /**
     * Constructor for a new ResponseFailedException.
     *
     * @param statusCode
     *          the received HTTP status code.
     * @param url
     *          the URL that was accessed.
     * @param response
     *          the response message (body) from the web server.
     */
    public ResponseFailedException(final int statusCode, final String url, final String response)
    {
        super("Received response with invalid HTTP status: " + statusCode);
        this.statusCode = statusCode;
        this.url = url;
        this.response = response;
    }
}
