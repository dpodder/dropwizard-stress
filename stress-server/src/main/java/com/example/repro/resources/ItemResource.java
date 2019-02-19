package com.example.repro.resources;

import com.example.repro.api.Item;
import java.util.List;
import java.util.function.Supplier;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
public class ItemResource
{
    private static final Supplier<WebApplicationException> NOT_FOUND =
        () -> new WebApplicationException(404);

    private final List<Item> items;

    ItemResource(final List<Item> items)
    {
        this.items = items;
    }

    @GET
    public List<Item> listItems()
    {
        return items;
    }

    @GET
    @Path("/{name}")
    public Item getItem(@PathParam("name") final String name)
    {
        return items.stream()
            .filter(item -> item.getName().equals(name))
            .findFirst()
            .orElseThrow(NOT_FOUND);
    }
}
