package com.example.repro.health;

import com.codahale.metrics.health.HealthCheck;

public class NopHealthCheck extends HealthCheck
{
    @Override
    protected Result check()
    {
        // Just return healthy for the purposes of this repro
        return Result.healthy();
    }
}
