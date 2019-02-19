package com.example.tools.stressclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.JerseyClientBuilder;

/**
 * Implements an HTTP REST client on top of Jersey.
 */
public class RemoteClient
{
    private static final String REQUEST_ID_HEADER = "debug-request-id";
    private static final Set<Integer> HTTP_OK_ONLY = new HashSet<>(Collections.singleton(200));

    private final Client client;
    private final ObjectMapper mapper;
    private final String baseUrl;

    /**
     * Constructs a {@code RemoteClient} instance.
     *
     * @param config
     *          The application configuration to use for initializing the Jersey client.
     */
    public RemoteClient(final Configuration config)
    {
        mapper = new ObjectMapper();
        baseUrl = config.getRemoteService();
        ClientBuilder clientBuilder = new JerseyClientBuilder()
            .connectTimeout((long) config.getConnectTimeout() * 1000, TimeUnit.MILLISECONDS)
            .readTimeout((long) config.getReadTimeout() * 1000, TimeUnit.MILLISECONDS);
        client = clientBuilder.build();
    }

    /**
     * Issues a GET request to a given URI, and returns the result as a JSON object.
     * @param requestId
     *          a unique request ID to forward to the client as {@code debug-request-id}.
     * @param uri
     *          the URI to GET.
     * @return
     *          the body of the response, parsed into a {@link JsonNode}.
     * @throws ResponseFailedException
     *          if the HTTP response is anything other than 200 ("OK").
     * @throws IOException
     *          if the response failed to be parsed as JSON.
     */
    public JsonNode getResourceAsJson(final String requestId, final String uri)
    throws ResponseFailedException, IOException
    {
        String response = getResource(requestId, uri);
        return mapper.readTree(response);
    }

    /**
     * Issues a GET request to a given URI, and returns the String response body.
     *
     * @param requestId
     *          a unique request ID to forward to the client as {@code debug-request-id}.
     * @param uri
     *          the URI to GET.
     * @return
     *          the body of the response, as a {@link String}.
     * @throws ResponseFailedException
     *          if the HTTP response is anything other than 200 ("OK").
     */
    public String getResource(final String requestId, final String uri)
    throws ResponseFailedException
    {
        return getResource(requestId, uri, HTTP_OK_ONLY);
    }

    private String getResource(
        final String requestId,
        final String uri,
        final Set<Integer> validResponses)
    throws ResponseFailedException
    {
        Builder requestBuilder = client.target(baseUrl + uri).request();
        requestBuilder.header(REQUEST_ID_HEADER, requestId);

        Response response = requestBuilder.buildGet().invoke();

        if (!validResponses.contains(response.getStatus()))
        {
            throw new ResponseFailedException(
                response.getStatus(), baseUrl + uri, response.readEntity(String.class));
        }

        return response.readEntity(String.class);
    }
}
