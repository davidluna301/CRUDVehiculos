package com.example.vehiculos.controller;

import com.example.vehiculos.model.Vehiculo;
import com.example.vehiculos.service.VehiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehiculos")
@CrossOrigin(origins = "*")
public class VehiculoController {
    
    @Autowired
    private VehiculoService vehiculoService;
    
    // GET - Obtener todos los vehículos
    @GetMapping
    public ResponseEntity<List<Vehiculo>> obtenerTodos() {
        List<Vehiculo> vehiculos = vehiculoService.obtenerTodos();
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Obtener vehículo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable String id) {
        Optional<Vehiculo> vehiculo = vehiculoService.obtenerPorId(id);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(vehiculo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError("Vehículo no encontrado con ID: " + id));
        }
    }
    
    // GET - Obtener vehículo por matrícula
    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<?> obtenerPorMatricula(@PathVariable String matricula) {
        Optional<Vehiculo> vehiculo = vehiculoService.obtenerPorMatricula(matricula);
        if (vehiculo.isPresent()) {
            return ResponseEntity.ok(vehiculo.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError("Vehículo no encontrado con matrícula: " + matricula));
        }
    }
    
    // POST - Crear nuevo vehículo
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Vehiculo vehiculo) {
        // Verificar si ya existe un vehículo con la misma matrícula
        if (vehiculoService.existePorMatricula(vehiculo.getMatricula())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError("Ya existe un vehículo con la matrícula: " + vehiculo.getMatricula()));
        }
        
        Vehiculo nuevoVehiculo = vehiculoService.guardar(vehiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVehiculo);
    }
    
    // PUT - Actualizar vehículo existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody Vehiculo vehiculo) {
        // Verificar si el vehículo existe
        if (!vehiculoService.existePorId(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError("Vehículo no encontrado con ID: " + id));
        }
        
        // Verificar si la matrícula ya existe en otro vehículo
        Optional<Vehiculo> vehiculoExistente = vehiculoService.obtenerPorMatricula(vehiculo.getMatricula());
        if (vehiculoExistente.isPresent() && !vehiculoExistente.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(crearRespuestaError("Ya existe otro vehículo con la matrícula: " + vehiculo.getMatricula()));
        }
        
        vehiculo.setId(id);
        Vehiculo vehiculoActualizado = vehiculoService.guardar(vehiculo);
        return ResponseEntity.ok(vehiculoActualizado);
    }
    
    // DELETE - Eliminar vehículo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        if (vehiculoService.eliminar(id)) {
            return ResponseEntity.ok(crearRespuestaExito("Vehículo eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError("Vehículo no encontrado con ID: " + id));
        }
    }
    
    // DELETE - Eliminar vehículo por matrícula
    @DeleteMapping("/matricula/{matricula}")
    public ResponseEntity<?> eliminarPorMatricula(@PathVariable String matricula) {
        if (vehiculoService.eliminarPorMatricula(matricula)) {
            return ResponseEntity.ok(crearRespuestaExito("Vehículo eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError("Vehículo no encontrado con matrícula: " + matricula));
        }
    }
    
    // GET - Buscar vehículos por marca
    @GetMapping("/marca/{marca}")
    public ResponseEntity<List<Vehiculo>> obtenerPorMarca(@PathVariable String marca) {
        List<Vehiculo> vehiculos = vehiculoService.obtenerPorMarca(marca);
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Buscar vehículos por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Vehiculo>> obtenerPorTipo(@PathVariable Vehiculo.TipoVehiculo tipo) {
        List<Vehiculo> vehiculos = vehiculoService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Buscar vehículos por año
    @GetMapping("/año/{año}")
    public ResponseEntity<List<Vehiculo>> obtenerPorAño(@PathVariable Integer año) {
        List<Vehiculo> vehiculos = vehiculoService.obtenerPorAño(año);
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Buscar vehículos por rango de precio
    @GetMapping("/precio")
    public ResponseEntity<List<Vehiculo>> obtenerPorRangoPrecio(
            @RequestParam Double min, 
            @RequestParam Double max) {
        List<Vehiculo> vehiculos = vehiculoService.obtenerPorRangoPrecio(min, max);
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Contar total de vehículos
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarVehiculos() {
        long total = vehiculoService.contarTotal();
        Map<String, Long> respuesta = new HashMap<>();
        respuesta.put("total", total);
        return ResponseEntity.ok(respuesta);
    }
    
    // GET - Búsqueda avanzada
    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<List<Vehiculo>> busquedaAvanzada(
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Vehiculo.TipoVehiculo tipo,
            @RequestParam(required = false) Integer añoMin,
            @RequestParam(required = false) Integer añoMax,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax) {
        
        List<Vehiculo> vehiculos = vehiculoService.busquedaAvanzada(marca, tipo, añoMin, añoMax, precioMin, precioMax);
        return ResponseEntity.ok(vehiculos);
    }
    
    // GET - Estadísticas
    @GetMapping("/estadisticas")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticas() {
        Map<String, Object> estadisticas = vehiculoService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
    
    // Métodos auxiliares para crear respuestas
    private Map<String, String> crearRespuestaError(String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", mensaje);
        return respuesta;
    }
    
    private Map<String, String> crearRespuestaExito(String mensaje) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}