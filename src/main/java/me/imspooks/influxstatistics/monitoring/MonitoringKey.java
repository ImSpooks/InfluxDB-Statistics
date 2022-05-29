package me.imspooks.influxstatistics.monitoring;

import lombok.Data;

/**
 * Created by Nick on 29 mei 2022.
 * Copyright © ImSpooks
 */
@Data
public class MonitoringKey {

    private final String key;
    private final int seconds;
}