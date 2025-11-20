package com.example.vehiculos.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String connectionString;

    @Bean
    public MongoClient mongoClient() {
        logger.info("🔗 Conectando a MongoDB Atlas: {}", 
            connectionString.replaceFirst("://([^:]+):([^@]+)@", "://***:***@"));
        
        MongoClient client = MongoClients.create(connectionString);
        
        // Probar la conexión
        try {
            client.listDatabaseNames().first();
            logger.info("✅ Conexión a MongoDB Atlas establecida correctamente");
        } catch (Exception e) {
            logger.error("❌ Error conectando a MongoDB Atlas: {}", e.getMessage());
            throw e;
        }
        
        return client;
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), "vehiculosdb");
    }
}