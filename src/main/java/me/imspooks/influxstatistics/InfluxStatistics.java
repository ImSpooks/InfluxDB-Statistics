package me.imspooks.influxstatistics;

import lombok.Getter;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nick on 29 mei 2022.
 * Copyright © ImSpooks
 */
public class InfluxStatistics {

    private final Map<String, String> tags = new HashMap<>();

    private InfluxDB influx;
    private BatchPoints batchPoints;

    @Getter private final List<StatisticPoint> statistics = new ArrayList<>();

    public InfluxStatistics(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("Tag size must be atleast one");
        }
        this.tags.putAll(tags);
    }

    public InfluxStatistics(String tag, String value) {
        this(Collections.singletonMap(tag, value));
    }

    public boolean connect(String host, String username, String password, String database) {
        this.influx = InfluxDBFactory.connect(host, username, password);
        this.batchPoints = BatchPoints.database(database).retentionPolicy("autogen").build();

        return true;
    }

    public void addStatistic(StatisticPoint statistic) {
        this.statistics.add(statistic);
    }

    public void removeStatistic(StatisticPoint statistic) {
        this.statistics.remove(statistic);
    }

    /**
     * @return Milliseconds on how long it took to upload stats
     */
    public long upload() {
        long now = System.currentTimeMillis();

        for (StatisticPoint statistic : this.statistics) {
            if (!statistic.shouldUpload()) {
                continue;
            }

            if (statistic.getValues().size() > 0) {
                statistic.setLastUpload(now);

                Map<String, String> tags = new HashMap<>(statistic.getTags());
                tags.putAll(this.tags);

                Point.Builder builder = Point.measurement(statistic.getName());
                builder.time(now, TimeUnit.MILLISECONDS);
                builder.tag(tags);

                for (Map.Entry<String, Double> entry : statistic.getValues().entrySet()) {
                    builder.addField(entry.getKey(), entry.getValue());
                }

                this.batchPoints.point(builder.build());
                statistic.reset();
            }
        }

        this.influx.write(this.batchPoints);
        this.batchPoints.getPoints().clear();

        return System.currentTimeMillis() - now;
    }

    public void close() {
        if (this.influx != null) {
            this.influx.close();
        }
    }
}