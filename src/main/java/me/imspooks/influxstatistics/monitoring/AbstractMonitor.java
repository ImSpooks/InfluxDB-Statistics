package me.imspooks.influxstatistics.monitoring;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sub-classes MUST be FINAL
 */
@Getter
public abstract class AbstractMonitor implements Runnable {

    /** A thread-safe map of averages. */
    private final Map<String, RollingAverage> averages = new ConcurrentHashMap<>();


    public AbstractMonitor(MonitoringKey pair, MonitoringKey... extraPairs) {
        this.averages.put(pair.getKey(), new RollingAverage(pair.getSeconds()));

        for (MonitoringKey extraPair : extraPairs) {
            this.averages.put(extraPair.getKey(), new RollingAverage(extraPair.getSeconds()));
        }
    }

    /**
     * The current value that will be added to our {@link RollingAverage}.
     *
     * @return A {@link BigDecimal} value.
     */
    public abstract BigDecimal getCurrentValue();

    /**
     * Resets the current value.
     */
    public abstract void resetValue();

    /**
     * Get a rolling {@link RollingAverage} by a given key.
     *
     * @param key A key to specify the {@link RollingAverage} instance.
     * @return A {@link RollingAverage} instance, or {@code null} if the key doesn't exist.
     */
    public RollingAverage getByKey(String key) {
        return this.averages.get(key);
    }

    /**
     * This method adds all the current values to our {@link RollingAverage} instances.
     */
    @Override
    public void run() {
        for (RollingAverage value : this.averages.values()) {
            value.add(this.getCurrentValue());
        }
        this.resetValue();
    }
}