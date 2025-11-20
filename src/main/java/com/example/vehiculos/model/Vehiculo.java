package com.example.vehiculos.model;

import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "vehiculos")
@CompoundIndex(name = "idx_marca_modelo_compuesto", def = "{'marca': 1, 'modelo': 1}")
@CompoundIndex(name = "idx_tipo_precio_compuesto", def = "{'tipo': 1, 'precio': 1}")
@CompoundIndex(name = "idx_marca_año_compuesto", def = "{'marca': 1, 'año': -1}")
public class Vehiculo {
    
    @Id
    private String id;

    @NotBlank(message = "La marca es obligatoria")
    @Field("marca")
    @Indexed(name = "idx_marca") // Índice individual para marca
    @TextIndexed(weight = 2) // Índice de texto con mayor peso para búsquedas
    private String marca;
    
    @NotBlank(message = "El modelo es obligatorio")
    @Field("modelo")
    @TextIndexed(weight = 2) // Índice de texto
    private String modelo;
    
    @NotBlank(message = "La matrícula es obligatoria")
    @Indexed(unique = true, name = "idx_matricula_unique") // Índice único
    @Field("matricula")
    private String matricula;
    
    @Min(value = 1900, message = "El año debe ser mayor o igual a 1900")
    @Max(value = 2030, message = "El año debe ser menor o igual a 2030")
    @Field("año")
    @Indexed(name = "idx_año_desc", direction = org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING)
    private Integer año;
    
    @NotBlank(message = "El color es obligatorio")
    @Field("color")
    @Indexed(name = "idx_color")
    @TextIndexed(weight = 1) // Índice de texto para color
    private String color;
    
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    @Field("precio")
    @Indexed(name = "idx_precio")
    private Double precio;
    
    @Field("tipo")
    @Indexed(name = "idx_tipo")
    private TipoVehiculo tipo;
    
    @Field("fecha_creacion")
    @Indexed(name = "idx_fecha_creacion", direction = org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING)
    private LocalDateTime fechaCreacion;
    
    @Field("fecha_actualizacion")
    @Indexed(name = "idx_fecha_actualizacion", direction = org.springframework.data.mongodb.core.index.IndexDirection.DESCENDING)
    private LocalDateTime fechaActualizacion;
    
    // Campo calculado - no se persiste pero se puede indexar si se necesita
    @Transient
    private String marcaModelo;
    
    public String getMarcaModelo() {
        return marca + " " + modelo;
    }

    // Enumerado para tipos de vehículo
    public enum TipoVehiculo {
        COCHE, MOTO, CAMION, FURGONETA, SUV
    }
    
    // Constructores
    public Vehiculo() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public Vehiculo(String marca, String modelo, String matricula, Integer año, 
                   String color, Double precio, TipoVehiculo tipo) {
        this();
        this.marca = marca;
        this.modelo = modelo;
        this.matricula = matricula;
        this.año = año;
        this.color = color;
        this.precio = precio;
        this.tipo = tipo;
    }
    
    // Getters y Setters (mantener los mismos que antes)
    public String getId() { return id; }
    public void setId(String id) { 
        this.id = id; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { 
        this.marca = marca; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { 
        this.modelo = modelo; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { 
        this.matricula = matricula; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public Integer getAño() { return año; }
    public void setAño(Integer año) { 
        this.año = año; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public String getColor() { return color; }
    public void setColor(String color) { 
        this.color = color; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { 
        this.precio = precio; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public TipoVehiculo getTipo() { return tipo; }
    public void setTipo(TipoVehiculo tipo) { 
        this.tipo = tipo; 
        this.fechaActualizacion = LocalDateTime.now();
    }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", matricula='" + matricula + '\'' +
                ", año=" + año +
                ", color='" + color + '\'' +
                ", precio=" + precio +
                ", tipo=" + tipo +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaActualizacion=" + fechaActualizacion +
                '}';
    }
}