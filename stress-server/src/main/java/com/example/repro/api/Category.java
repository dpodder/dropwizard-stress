package com.example.repro.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JsonInclude(Include.NON_NULL)
public final class Category implements Cloneable
{
    private UUID id;
    private String name;
    private List<String> tags;
    private List<Item> items;

    public Category()
    {
        id = null;
        name = null;
        tags = new ArrayList<>();
        items = new ArrayList<>();
    }

    public Category(
        final UUID id, final String name, final List<String> tags, final List<Item> items)
    {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.items = items;
    }

    @Override
    public Category clone()
    {
        try
        {
            Category copy = (Category) super.clone();
            copy.setTags(new ArrayList<>(tags));
            copy.setItems(items.stream().map(Item::clone).collect(Collectors.toList()));
            return copy;
        }
        catch (CloneNotSupportedException ex)
        {
            // Clone is supported... and we're final... let's swallow the exception
            throw new RuntimeException(ex);
        }
    }

    public UUID getId()
    {
        return id;
    }

    public void setId(final UUID id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public List<String> getTags()
    {
        return tags;
    }

    public void setTags(final List<String> tags)
    {
        this.tags = tags;
    }

    public List<Item> getItems()
    {
        return items;
    }

    public void setItems(final List<Item> items)
    {
        this.items = items;
    }
}
