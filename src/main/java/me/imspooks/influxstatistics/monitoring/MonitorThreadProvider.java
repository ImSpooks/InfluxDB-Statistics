package me.imspooks.influxstatistics.monitoring;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Nick on 29 Dec 2021.
 * Copyright © ImSpooks
 */
public class MonitorThreadProvider {

    private static int THREADS = 1;

    /** The executor used to monitor & calculate rolling averages. */
    public static final ScheduledExecutorService EXECUTOR =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName(String.format("Polar Monitoring Thread - #%d", THREADS++));
                return thread;
            });
}