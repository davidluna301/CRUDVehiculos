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
import java.util.List;

@Configuration
public class IndexConfig {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoMappingContext mongoMappingContext;

    @PostConstruct
    public void initIndexes() {
        System.out.println("🔧 Configurando índices de MongoDB...");
        try {
            crearIndicesPersonalizados();
            System.out.println("✅ Índices configurados correctamente");
        } catch (Exception e) {
            System.out.println("⚠️  Algunos índices ya existen, continuando...");
            // No detener la aplicación por errores de índices
        }
    }

    /**
     * Índices personalizados para optimizar las consultas frecuentes
     */
    private void crearIndicesPersonalizados() {
        try {
            IndexOperations indexOps = mongoTemplate.indexOps(Vehiculo.class);
            
            // Verificar índices existentes
            List<org.springframework.data.mongodb.core.index.IndexInfo> existingIndexes = indexOps.getIndexInfo();
            System.out.println("📊 Índices existentes: " + existingIndexes.size());
            
            // 1. Índice único para matrícula - solo si no existe
            boolean matriculaIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getIndexFields().stream()
                    .anyMatch(field -> field.getKey().equals("matricula")));
            
            if (!matriculaIndexExists) {
                indexOps.ensureIndex(new Index().on("matricula", org.springframework.data.domain.Sort.Direction.ASC).unique().named("idx_matricula_unique"));
                System.out.println("✅ Índice de matrícula creado");
            } else {
                System.out.println("ℹ️  Índice de matrícula ya existe");
            }
            
            // 2. Índice compuesto para búsquedas por marca y modelo
            boolean marcaModeloIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getName() != null && index.getName().equals("idx_marca_modelo"));
            
            if (!marcaModeloIndexExists) {
                indexOps.ensureIndex(new Index().on("marca", org.springframework.data.domain.Sort.Direction.ASC)
                                               .on("modelo", org.springframework.data.domain.Sort.Direction.ASC)
                                               .named("idx_marca_modelo"));
                System.out.println("✅ Índice marca-modelo creado");
            }
            
            // 3. Índice para búsquedas por tipo
            boolean tipoIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getName() != null && index.getName().equals("idx_tipo"));
            
            if (!tipoIndexExists) {
                indexOps.ensureIndex(new Index().on("tipo", org.springframework.data.domain.Sort.Direction.ASC)
                                               .named("idx_tipo"));
                System.out.println("✅ Índice de tipo creado");
            }
            
            // 4. Índice para búsquedas por año
            boolean añoIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getName() != null && index.getName().equals("idx_año"));
            
            if (!añoIndexExists) {
                indexOps.ensureIndex(new Index().on("año", org.springframework.data.domain.Sort.Direction.DESC)
                                               .named("idx_año"));
                System.out.println("✅ Índice de año creado");
            }
            
            // 5. Índice para búsquedas por precio
            boolean precioIndexExists = existingIndexes.stream()
                .anyMatch(index -> index.getName() != null && index.getName().equals("idx_precio"));
            
            if (!precioIndexExists) {
                indexOps.ensureIndex(new Index().on("precio", org.springframework.data.domain.Sort.Direction.ASC)
                                               .named("idx_precio"));
                System.out.println("✅ Índice de precio creado");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error creando índices: " + e.getMessage());
            // No relanzar la excepción para permitir que la aplicación continúe
        }
    }
}