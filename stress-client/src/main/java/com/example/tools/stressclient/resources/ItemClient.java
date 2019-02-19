package com.example.tools.stressclient.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.example.tools.stressclient.RemoteClient;
import com.example.tools.stressclient.ResponseFailedException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

/**
 * Lightweight wrapper around RemoteClient to get an Item object.
 */
@RequiredArgsConstructor
public class ItemClient
{
    /**
     * The underlying {@link RemoteClient}.
     */
    private final RemoteClient client;

    /**
     * Gets an item resource by name.
     * @param requestId
     *          a unique request ID to forward to the client.
     * @param categoryName
     *          name of the Category containing the desired Item.
     * @param itemName
     *          name of the Item to GET.
     * @return
     *          the fetched Item, as a {@link JsonNode}.
     * @throws ResourceMismatchException
     *          if a wrong or malformed resource object is received.
     * @throws ResponseFailedException
     *          if the remote server returns an unexpected HTTP code.
     * @throws IOException
     *          if the HTTP call's response fails to be parsed as valid JSON.
     */
    public JsonNode getItem(
        final String requestId, final String categoryName, final String itemName)
    throws ResourceMismatchException, ResponseFailedException, IOException
    {
        JsonNode root = client.getResourceAsJson(
            requestId, "/category/" + categoryName + "/item/" + itemName);
        JsonNode nameElement = root.get("name");
        if (nameElement == null || !nameElement.isTextual())
        {
            throw new ResourceMismatchException(
                "Item: missing name", itemName, root.toString());
        }
        if (!itemName.equals(nameElement.textValue()))
        {
            throw new ResourceMismatchException(
                "Item: wrong object", itemName, nameElement.textValue());
        }
        validateItem(root, itemName);
        return root;
    }

    private void validateItem(JsonNode itemJson, String handle)
    throws ResourceMismatchException
    {
        if (!itemJson.has("id"))
        {
            throw new ResourceMismatchException(
                "Missing id in Item " + handle, "id", itemJson.toString());
        }
        if (!itemJson.get("id").isTextual())
        {
            throw new ResourceMismatchException(
                "Item id: not a string in Item " + handle,
                "\"...\"",
                itemJson.toString());
        }
        if (!itemJson.has("tags"))
        {
            throw new ResourceMismatchException(
                "Missing tags in Item " + handle, "tags", itemJson.toString());
        }
        if (!itemJson.get("tags").isArray())
        {
            throw new ResourceMismatchException(
                "Item tags: not an array in Item " + handle,
                "[...]",
                itemJson.toString());
        }
    }
}
