package me.imspooks.influxstatistics.test;

import me.imspooks.influxstatistics.InfluxStatistics;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nick on 29 mei 2022.
 * Copyright © ImSpooks
 */
public class InfluxTest {

    public static void main(String[] args) {
        String host = args[0];
        String username = args[1];
        String password = args[2];
        String database = args[3];

        InfluxStatistics statistics = new InfluxStatistics("type", "test");

        Runtime.getRuntime().addShutdownHook(new Thread(statistics::close));

        TestStatistic testStatistic = new TestStatistic();

        statistics.connect(host, username, password, database);
        statistics.addStatistic(testStatistic);

        new Thread(() -> {
            while (true) {
                testStatistic.addValue("random-value", ThreadLocalRandom.current().nextInt(100));

                try {
                    System.out.println("Sending statistics took " + statistics.upload() + " ms.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}