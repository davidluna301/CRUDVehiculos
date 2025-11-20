package com.example.vehiculos.service;

import com.example.vehiculos.model.Vehiculo;
import com.example.vehiculos.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VehiculoService {
    
    @Autowired
    private VehiculoRepository vehiculoRepository;
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // Obtener todos los vehículos con ordenamiento por índice
    public List<Vehiculo> obtenerTodos() {
        return vehiculoRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion"));
    }
    
    // Obtener vehículo por ID
    public Optional<Vehiculo> obtenerPorId(String id) {
        return vehiculoRepository.findById(id);
    }
    
    // Obtener vehículo por matrícula (usa índice único)
    public Optional<Vehiculo> obtenerPorMatricula(String matricula) {
        return vehiculoRepository.findByMatricula(matricula);
    }
    
    // Guardar vehículo (crear o actualizar)
    public Vehiculo guardar(Vehiculo vehiculo) {
        return vehiculoRepository.save(vehiculo);
    }
    
    // Eliminar vehículo por ID
    public boolean eliminar(String id) {
        if (vehiculoRepository.existsById(id)) {
            vehiculoRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    // Eliminar vehículo por matrícula (usa índice único)
    public boolean eliminarPorMatricula(String matricula) {
        Optional<Vehiculo> vehiculo = vehiculoRepository.findByMatricula(matricula);
        if (vehiculo.isPresent()) {
            vehiculoRepository.deleteByMatricula(matricula);
            return true;
        }
        return false;
    }
    
    // Obtener vehículos por marca (usa índice de marca)
    public List<Vehiculo> obtenerPorMarca(String marca) {
        return vehiculoRepository.findByMarca(marca);
    }
    
    // Obtener vehículos por tipo (usa índice de tipo)
    public List<Vehiculo> obtenerPorTipo(Vehiculo.TipoVehiculo tipo) {
        return vehiculoRepository.findByTipo(tipo);
    }
    
    // Obtener vehículos por año (usa índice de año)
    public List<Vehiculo> obtenerPorAño(Integer año) {
        return vehiculoRepository.findByAñoGreaterThanEqual(año);
    }
    
    // Obtener vehículos por rango de precio (usa índice de precio)
    public List<Vehiculo> obtenerPorRangoPrecio(Double precioMin, Double precioMax) {
        return vehiculoRepository.findByPrecioBetween(precioMin, precioMax);
    }
    
    // Verificar si existe un vehículo con una matrícula (usa índice único)
    public boolean existePorMatricula(String matricula) {
        return vehiculoRepository.existsByMatricula(matricula);
    }
    
    // Verificar si existe un vehículo con un ID
    public boolean existePorId(String id) {
        return vehiculoRepository.existsById(id);
    }
    
    // Contar total de vehículos
    public long contarTotal() {
        return vehiculoRepository.count();
    }
    
    // Obtener vehículos con precio mayor a (usa índice de precio)
    public List<Vehiculo> obtenerPorPrecioMayorQue(Double precio) {
        return vehiculoRepository.findByPrecioGreaterThan(precio);
    }
    
    // CONSULTAS OPTIMIZADAS CON MONGOTEMPLATE (USANDO ÍNDICES)
    
    /**
     * Búsqueda optimizada por marca y modelo (usa índice compuesto)
     */
    public List<Vehiculo> buscarPorMarcaYModelo(String marca, String modelo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("marca").is(marca).and("modelo").is(modelo));
        query.with(Sort.by(Sort.Direction.DESC, "año")); // Usa índice de año
        return mongoTemplate.find(query, Vehiculo.class);
    }
    
    /**
     * Búsqueda por tipo y rango de precio (usa índice compuesto)
     */
    public List<Vehiculo> buscarPorTipoYRangoPrecio(Vehiculo.TipoVehiculo tipo, Double minPrecio, Double maxPrecio) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tipo").is(tipo)
                                 .and("precio").gte(minPrecio).lte(maxPrecio));
        query.with(Sort.by(Sort.Direction.ASC, "precio")); // Usa índice de precio
        return mongoTemplate.find(query, Vehiculo.class);
    }
    
    /**
     * Búsqueda por marca y año (usa índice compuesto)
     */
    public List<Vehiculo> buscarPorMarcaYAño(String marca, Integer añoMin, Integer añoMax) {
        Query query = new Query();
        query.addCriteria(Criteria.where("marca").is(marca)
                                 .and("año").gte(añoMin).lte(añoMax));
        query.with(Sort.by(Sort.Direction.DESC, "año")); // Usa índice de año
        return mongoTemplate.find(query, Vehiculo.class);
    }
    
    /**
     * Búsqueda avanzada usando múltiples índices
     */
    public List<Vehiculo> busquedaAvanzada(String marca, Vehiculo.TipoVehiculo tipo, 
                                          Integer añoMin, Integer añoMax, 
                                          Double precioMin, Double precioMax) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        
        if (marca != null && !marca.isEmpty()) {
            criteria.and("marca").is(marca);
        }
        
        if (tipo != null) {
            criteria.and("tipo").is(tipo);
        }
        
        if (añoMin != null && añoMax != null) {
            criteria.and("año").gte(añoMin).lte(añoMax);
        }
        
        if (precioMin != null && precioMax != null) {
            criteria.and("precio").gte(precioMin).lte(precioMax);
        }
        
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Direction.DESC, "año").and(Sort.by(Sort.Direction.ASC, "precio")));
        
        return mongoTemplate.find(query, Vehiculo.class);
    }
    
    /**
     * Obtener estadísticas usando agregaciones (óptimo para MongoDB)
     */
    public Map<String, Object> obtenerEstadisticas() {
        return Map.of(
            "totalVehiculos", contarTotal(),
            "marcas", obtenerConteoPorMarca(),
            "tipos", obtenerConteoPorTipo(),
            "precioPromedio", obtenerPrecioPromedio()
        );
    }
    
    private Map<String, Long> obtenerConteoPorMarca() {
        List<Vehiculo> todos = obtenerTodos();
        return todos.stream()
                .collect(Collectors.groupingBy(Vehiculo::getMarca, Collectors.counting()));
    }
    
    private Map<String, Long> obtenerConteoPorTipo() {
        List<Vehiculo> todos = obtenerTodos();
        return todos.stream()
                .collect(Collectors.groupingBy(v -> v.getTipo().name(), Collectors.counting()));
    }
    
    private Double obtenerPrecioPromedio() {
        List<Vehiculo> todos = obtenerTodos();
        return todos.stream()
                .mapToDouble(Vehiculo::getPrecio)
                .average()
                .orElse(0.0);
    }
}