package com.example.repro.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonInclude(Include.NON_NULL)
public final class Item implements Cloneable
{
    private UUID id;
    private String name;
    private List<String> tags;

    public Item()
    {
        id = null;
        name = null;
        tags = new ArrayList<>();
    }

    @Override
    public Item clone()
    {
        try
        {
            Item copy = (Item) super.clone();
            copy.setTags(new ArrayList<>(tags));
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
}
