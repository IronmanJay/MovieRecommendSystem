package com.IronmanJay.business.utils;

import com.mongodb.MongoClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import redis.clients.jedis.Jedis;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

// 通过Configure类来实例Bean
@Configuration
public class Configure {

    private String jedisHost;
    private String mongoHost;
    private int mongoPort;
    private String esClusterName;
    private String esHost;
    private int esPort;

    public Configure() throws IOException {
        // 加载配置文件
        Properties properties = new Properties();
        Resource resource = new ClassPathResource("application.properties");
        // 具体加载了配置文件
        properties.load(new FileInputStream(resource.getFile()));
        this.jedisHost = properties.getProperty("redis.host");
        this.mongoHost = properties.getProperty("mongo.host");
        this.mongoPort = Integer.parseInt(properties.getProperty("mongo.port"));
        this.esClusterName = properties.getProperty("es.cluster.name");
        this.esHost = properties.getProperty("es.host");
        this.esPort = Integer.parseInt(properties.getProperty("es.port"));
        // 提取配置属性
    }

    // 将Jedia注册为bean
    @Bean("jedis")
    public Jedis getJedis() {
        Jedis jedis = new Jedis(this.jedisHost);
        return jedis;
    }

    // 将Jedia注册为bean
    @Bean("mongoClient")
    public MongoClient getMongoClient() {
        MongoClient mongoClient = new MongoClient(this.mongoHost, this.mongoPort);
        return mongoClient;
    }

    // 将esClient注册为bean
    @Bean(name = "transportClient")
    public TransportClient getTransportClient() throws UnknownHostException {
        Settings settings = Settings.builder().put("cluster.name", this.esClusterName).build();
        TransportClient esClient = new PreBuiltTransportClient(settings);
        esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(this.esHost), this.esPort));
        return esClient;
    }

}
