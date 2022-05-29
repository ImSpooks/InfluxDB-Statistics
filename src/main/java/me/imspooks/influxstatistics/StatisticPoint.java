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

    private final Map<String, Number> values = new HashMap<>();
    private final Map<String, String> tags = new HashMap<>();

    public long lastUpload;
    public long delayBetweenUploads;

    public void addValue(String name, Number value) {
        if (name != null && !name.isEmpty() && value != null) {
            values.put(name, value);
        }
    }

    public Number getValue(String name) {
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