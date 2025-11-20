package com.example.vehiculos.config;

import com.example.vehiculos.model.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Configuration
public class IndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @PostConstruct
    public void initIndexes() {
        System.out.println("🔧 Configurando índices de MongoDB...");
        crearIndicesAutomaticos();
        crearIndicesPersonalizados();
        System.out.println("✅ Índices configurados correctamente");
    }

    /**
     * Índices automáticos basados en las anotaciones @Indexed de las entidades
     */
    private void crearIndicesAutomaticos() {
        try {
            IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
            IndexOperations indexOps = mongoTemplate.indexOps(Vehiculo.class);
            resolver.resolveIndexFor(Vehiculo.class).forEach(indexOps::ensureIndex);
        } catch (Exception e) {
            System.out.println("⚠️  Error creando índices automáticos: " + e.getMessage());
        }
    }

    /**
     * Índices personalizados para optimizar las consultas frecuentes
     */
    private void crearIndicesPersonalizados() {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps(Vehiculo.class);
            
            // 1. Índice único para matrícula (ya está en la entidad, pero lo reforzamos)
            indexOps.ensureIndex(new Index().on("matricula", org.springframework.data.domain.Sort.Direction.ASC).unique());
            
            // 2. Índice compuesto para búsquedas por marca y modelo
            indexOps.ensureIndex(new Index().on("marca", org.springframework.data.domain.Sort.Direction.ASC)
                                           .on("modelo", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_marca_modelo"));
            
            // 3. Índice para búsquedas por tipo
            indexOps.ensureIndex(new Index().on("tipo", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_tipo"));
            
            // 4. Índice para búsquedas por año (rango)
            indexOps.ensureIndex(new Index().on("año", org.springframework.data.domain.Sort.Direction.DESC)
                                           .named("idx_año"));
            
            // 5. Índice para búsquedas por precio (rango)
            indexOps.ensureIndex(new Index().on("precio", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_precio"));
            
            // 6. Índice compuesto para tipo y precio (útil para filtros combinados)
            indexOps.ensureIndex(new Index().on("tipo", org.springframework.data.domain.Sort.Direction.ASC)
                                           .on("precio", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_tipo_precio"));
            
            // 7. Índice para búsquedas por color (texto)
            indexOps.ensureIndex(new Index().on("color", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_color"));
            
            // 8. Índice compuesto para marca y año
            indexOps.ensureIndex(new Index().on("marca", org.springframework.data.domain.Sort.Direction.ASC)
                                           .on("año", org.springframework.data.domain.Sort.Direction.DESC)
                                           .named("idx_marca_año"));
            
            // 9. Índice para fechas (útil para ordenar por fecha de creación)
            indexOps.ensureIndex(new Index().on("fecha_creacion", org.springframework.data.domain.Sort.Direction.DESC)
                                           .named("idx_fecha_creacion"));
            
            // 10. Índice compuesto para búsquedas avanzadas
            indexOps.ensureIndex(new Index().on("marca", org.springframework.data.domain.Sort.Direction.ASC)
                                           .on("tipo", org.springframework.data.domain.Sort.Direction.ASC)
                                           .on("año", org.springframework.data.domain.Sort.Direction.DESC)
                                           .on("precio", org.springframework.data.domain.Sort.Direction.ASC)
                                           .named("idx_busqueda_avanzada"));
            
            System.out.println("📈 Índices personalizados creados exitosamente");
            
        } catch (Exception e) {
            System.out.println("❌ Error creando índices personalizados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}