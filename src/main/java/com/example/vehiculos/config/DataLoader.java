package com.example.vehiculos.config;

import com.example.vehiculos.model.Vehiculo;
import com.example.vehiculos.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private VehiculoRepository vehiculoRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Limpiar la colección solo si está vacía
        if (vehiculoRepository.count() == 0) {
            vehiculoRepository.deleteAll();
            
            // Datos de prueba para MongoDB
            Vehiculo v1 = new Vehiculo("Toyota", "Corolla", "ABC123", 2022, "Rojo", 25000.0, Vehiculo.TipoVehiculo.COCHE);
            Vehiculo v2 = new Vehiculo("Honda", "Civic", "DEF456", 2023, "Azul", 27000.0, Vehiculo.TipoVehiculo.COCHE);
            Vehiculo v3 = new Vehiculo("Yamaha", "MT-07", "GHI789", 2021, "Negro", 8000.0, Vehiculo.TipoVehiculo.MOTO);
            Vehiculo v4 = new Vehiculo("Ford", "Ranger", "JKL012", 2020, "Blanco", 35000.0, Vehiculo.TipoVehiculo.CAMION);
            Vehiculo v5 = new Vehiculo("Volkswagen", "Transporter", "MNO345", 2019, "Gris", 30000.0, Vehiculo.TipoVehiculo.FURGONETA);
            Vehiculo v6 = new Vehiculo("BMW", "X5", "PQR678", 2023, "Negro", 65000.0, Vehiculo.TipoVehiculo.SUV);
            
            vehiculoRepository.save(v1);
            vehiculoRepository.save(v2);
            vehiculoRepository.save(v3);
            vehiculoRepository.save(v4);
            vehiculoRepository.save(v5);
            vehiculoRepository.save(v6);
            
            System.out.println("✅ Datos de prueba cargados en MongoDB. Total: " + vehiculoRepository.count() + " vehículos.");
        } else {
            System.out.println("ℹ️  MongoDB ya contiene datos. Total: " + vehiculoRepository.count() + " vehículos.");
        }
    }
}