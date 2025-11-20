package com.example.vehiculos.repository;

import com.example.vehiculos.model.Vehiculo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {
    
    // Buscar vehículo por matrícula
    Optional<Vehiculo> findByMatricula(String matricula);
    
    // Buscar vehículos por marca
    List<Vehiculo> findByMarca(String marca);
    
    // Buscar vehículos por tipo
    List<Vehiculo> findByTipo(Vehiculo.TipoVehiculo tipo);
    
    // Buscar vehículos por año mayor o igual
    List<Vehiculo> findByAñoGreaterThanEqual(Integer año);
    
    // Buscar vehículos por rango de precio
    List<Vehiculo> findByPrecioBetween(Double precioMin, Double precioMax);
    
    // Buscar vehículos por marca y modelo
    List<Vehiculo> findByMarcaAndModelo(String marca, String modelo);
    
    // Consulta personalizada para buscar vehículos por color (case insensitive)
    @Query("{ 'color': { $regex: ?0, $options: 'i' } }")
    List<Vehiculo> findByColorContainingIgnoreCase(String color);
    
    // Verificar si existe un vehículo con una matrícula
    boolean existsByMatricula(String matricula);
    
    // Buscar vehículos por marca (case insensitive)
    @Query("{ 'marca': { $regex: ?0, $options: 'i' } }")
    List<Vehiculo> findByMarcaIgnoreCase(String marca);
    
    // Buscar vehículos con precio mayor que
    List<Vehiculo> findByPrecioGreaterThan(Double precio);
    
    // Eliminar vehículo por matrícula
    void deleteByMatricula(String matricula);
}