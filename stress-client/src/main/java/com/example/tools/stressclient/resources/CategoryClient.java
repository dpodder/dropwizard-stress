package com.example.tools.stressclient.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.tools.stressclient.RemoteClient;
import com.example.tools.stressclient.ResponseFailedException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

/**
 * Lightweight wrapper around RemoteClient to get a Category object.
 */
@RequiredArgsConstructor
public class CategoryClient
{
    /**
     * The underlying {@link RemoteClient}.
     */
    private final RemoteClient client;

    /**
     * Gets a Category resource by name.
     * @param requestId
     *          a unique request ID to forward to the client.
     * @param categoryName
     *          name of the Category to GET.
     * @return
     *          the fetched Category, as a {@link JsonNode}.
     * @throws ResourceMismatchException
     *          if a wrong or malformed resource object is received.
     * @throws ResponseFailedException
     *          if the remote server returns an unexpected HTTP code.
     * @throws IOException
     *          if the HTTP call's response fails to be parsed as valid JSON.
     */
    public JsonNode getCategory(final String requestId, final String categoryName)
    throws ResourceMismatchException, ResponseFailedException, IOException
    {
        JsonNode root = client.getResourceAsJson(requestId, "/category/" + categoryName);
        JsonNode nameElement = root.get("name");
        if (nameElement == null || !nameElement.isTextual())
        {
            throw new ResourceMismatchException(
                "Category: missing name", categoryName, root.toString());
        }
        if (!categoryName.equals(nameElement.textValue()))
        {
            throw new ResourceMismatchException(
                "Category: wrong object", categoryName, nameElement.textValue());
        }
        validateCategory(root, categoryName);
        return root;
    }

    private void validateCategory(JsonNode categoryJson, String handle)
    throws ResourceMismatchException
    {
        if (!categoryJson.has("id"))
        {
            throw new ResourceMismatchException(
                "Missing id in Category " + handle,
                "id",
                categoryJson.toString());
        }
        if (!categoryJson.get("id").isTextual())
        {
            throw new ResourceMismatchException(
                "Category id: not a string in Category " + handle,
                "\"...\"",
                categoryJson.toString());
        }
        if (!categoryJson.has("tags"))
        {
            throw new ResourceMismatchException(
                "Missing tags in Category " + handle,
                "tags",
                categoryJson.toString());
        }
        if (!categoryJson.get("tags").isArray())
        {
            throw new ResourceMismatchException(
                "Category tags: not an array in Category " + handle,
                "[...]",
                categoryJson.toString());
        }
    }
}
