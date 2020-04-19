package com.IronmanJay.kafkastream;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;

import java.util.Properties;

public class Application {
    public static void main(String[] args) {
        String brokers = "linux:9092";
        String zookeepers = "linux:2181";

        // 输入和输出的topic
        String from = "log";
        String to = "recommender";

        // 定义kafka streaming的配置
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        settings.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeepers);
        settings.put(StreamsConfig.TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);

        // 创建 kafka stream 配置对象
        StreamsConfig config = new StreamsConfig(settings);

        // 创建一个拓扑建构器
        TopologyBuilder builder = new TopologyBuilder();

        // 定义流处理的拓扑结构
        builder.addSource("SOURCE", from)
                .addProcessor("PROCESSOR", () -> new LogProcessor(), "SOURCE")
                .addSink("SINK", to, "PROCESSOR");

        KafkaStreams streams = new KafkaStreams(builder, config);

        streams.start();

        System.out.println("Kafka stream started!>>>>>>>>>>>");

    }
}
