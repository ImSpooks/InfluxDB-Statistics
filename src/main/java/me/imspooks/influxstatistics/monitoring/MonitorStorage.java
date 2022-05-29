package me.imspooks.influxstatistics.monitoring;

import lombok.Getter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nick on 29 Dec 2021.
 * Copyright © ImSpooks
 */
@Getter
public class MonitorStorage {

    @Getter private static final MonitorStorage instance = new MonitorStorage();
    private static boolean ACTIVE = false;

    /** List of active monitors */
    private final List<AbstractMonitor> monitors = new ArrayList<>();

    /**
     * Add a {@link AbstractMonitor} that will monitor a specified value.
     *
     * @param monitor A {@link AbstractMonitor} to add
     */
    public void addMonitor(AbstractMonitor monitor) {
        if (this.getMonitor(monitor.getClass()) != null) {
            throw new IllegalArgumentException("A monitor already exists with class \"" + monitor.getClass().getName() + "\"");
        }
        if (!Modifier.isFinal(monitor.getClass().getModifiers())) {
            throw new IllegalArgumentException("Monitor class must be final of \"" + monitor.getClass().getName() + "\"");
        }
        this.monitors.add(monitor);
    }

    /**
     * Get a {@link AbstractMonitor}-extended instance by a class.
     *
     * @param clazz The {@link Class<T>} of the monitor that extends {@link AbstractMonitor}.
     * @return A monitor instance, or {@code null} if no monitor was found.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractMonitor> T getMonitor(Class<T> clazz) {
        for (AbstractMonitor monitor : this.monitors) {
            if (clazz.isInstance(monitor)) {
                return (T) monitor;
            }
        }
        return null;
    }

    /**
     * Creates the thread that will handle all monitors with a fixed rate of 1 second.
     */
    public void initialize() {
        // Make sure we can only call this once, or else it will break.
        if (ACTIVE) {
            throw new IllegalAccessError("Monitoring is already enabled.");
        }
        ACTIVE = true;

        MonitorThreadProvider.EXECUTOR.scheduleAtFixedRate(() -> {
            // Run each monitor
            for (AbstractMonitor monitor : this.monitors) {
                monitor.run();
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}