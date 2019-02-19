package com.example.repro.resources;

import com.example.repro.api.Category;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/category")
public class CategoryResource
{
    private static final Supplier<WebApplicationException> NOT_FOUND =
        () -> new WebApplicationException(404);

    private final List<Category> categoryStore;

    public CategoryResource(final List<Category> categories)
    {
        // NB: list is shared across threads. Make an immutable deep copy of the list for safety.
        categoryStore = Collections.unmodifiableList(
            categories.stream().map(Category::clone).collect(Collectors.toList()));
    }

    private static Category stripItemList(final Category from)
    {
        return new Category(from.getId(), from.getName(), from.getTags(), null);
    }

    private Category findCategory(final String name)
    {
        return categoryStore.stream()
            .filter(category -> category.getName().equals(name))
            .findFirst()
            .orElseThrow(NOT_FOUND);
    }

    @GET
    public List<Category> listCategories()
    {
        return categoryStore.stream()
            .map(CategoryResource::stripItemList)
            .collect(Collectors.toList());
    }

    @GET
    @Path("/{name}")
    public Category getCategory(@PathParam("name") final String name)
    {
        return stripItemList(findCategory(name));
    }

    @Path("/{name}/item")
    public ItemResource getCategoryItem(@PathParam("name") final String name)
    {
        return new ItemResource(findCategory(name).getItems());
    }
}
