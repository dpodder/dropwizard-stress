package com.example.repro;

import com.example.repro.api.Category;
import com.example.repro.health.NopHealthCheck;
import com.example.repro.resources.CategoryResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import java.util.List;

public class StressApplication extends Application<StressConfiguration>
{
    public static void main(final String[] args) throws Exception
    {
        new StressApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "stress-server";
    }

    @Override
    public void initialize(final Bootstrap<StressConfiguration> bootstrap)
    {
        // Nothing to do
    }

    @Override
    public void run(final StressConfiguration configuration, final Environment environment)
    {
        List<Category> categories = configuration.getCategories();
        CategoryResource rootResource = new CategoryResource(categories);
        environment.jersey().register(rootResource);
        NopHealthCheck healthCheck = new NopHealthCheck();
        environment.healthChecks().register("nop", healthCheck);
    }
}
