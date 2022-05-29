package me.imspooks.influxstatistics;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 29 mei 2022.
 * Copyright © ImSpooks
 */
@Data
public abstract class StatisticPoint {

    private final Map<String, Double> values = new HashMap<>();
    private final Map<String, String> tags = new HashMap<>();

    public long lastUpload;
    public long delayBetweenUploads;

    public void addValue(String name, double value) {
        if (name != null && !name.isEmpty()) {
            this.values.put(name, this.values.getOrDefault(name, 0.0) + value);
        }
    }

    public double getValue(String name) {
        return this.values.get(name);
    }

    public void reset() {
        this.values.clear();
    }

    public void addTag(String name, String value) {
        this.tags.put(name, value);
    }

    public boolean shouldUpload() {
        return lastUpload == 0 || delayBetweenUploads == 0 || System.currentTimeMillis() >= lastUpload + delayBetweenUploads;
    }

    public abstract String getName();
}