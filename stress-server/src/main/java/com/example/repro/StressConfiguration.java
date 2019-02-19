package com.example.repro;

import com.example.repro.api.Category;
import io.dropwizard.Configuration;
import java.util.ArrayList;
import java.util.List;

public class StressConfiguration extends Configuration
{
    private List<Category> categories;

    public StressConfiguration()
    {
        categories = new ArrayList<>();
    }

    public List<Category> getCategories()
    {
        return categories;
    }

    public void setCategories(final List<Category> categories)
    {
        this.categories = categories;
    }
}
