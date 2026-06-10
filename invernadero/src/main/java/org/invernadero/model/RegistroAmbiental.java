package org.invernadero.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Registros_Ambientales")
public class RegistroAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    @ManyToOne
    @JoinColumn(name = "id_zona", nullable = false)
    private Zona zona;

    @Column(name = "fecha_hora", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(name = "temperatura_interior", nullable = false, precision = 4, scale = 2)
    private BigDecimal temperaturaInterior;

    @Column(name = "temperatura_exterior", nullable = false, precision = 4, scale = 2)
    private BigDecimal temperaturaExterior;

    @Column(name = "humedad_relativa", nullable = false, precision = 5, scale = 2)
    private BigDecimal humedadRelativa;

    @Column(name = "humedad_suelo", nullable = false, precision = 5, scale = 2)
    private BigDecimal humedadSuelo;

    @Column(name = "radiacion_solar", nullable = false, precision = 6, scale = 2)
    private BigDecimal radiacionSolar;

    @Column(name = "indice_uv", nullable = false, precision = 3, scale = 1)
    private BigDecimal indiceUv;

    @Column(name = "estado_ventilacion", nullable = false, length = 20)
    private String estadoVentilacion;

    public RegistroAmbiental() {
    }

    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }

    public Zona getZona() { return zona; }
    public void setZona(Zona zona) { this.zona = zona; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    public BigDecimal getTemperaturaInterior() { return temperaturaInterior; }
    public void setTemperaturaInterior(BigDecimal temperaturaInterior) { this.temperaturaInterior = temperaturaInterior; }

    public BigDecimal getTemperaturaExterior() { return temperaturaExterior; }
    public void setTemperaturaExterior(BigDecimal temperaturaExterior) { this.temperaturaExterior = temperaturaExterior; }

    public BigDecimal getHumedadRelativa() { return humedadRelativa; }
    public void setHumedadRelativa(BigDecimal humedadRelativa) { this.humedadRelativa = humedadRelativa; }

    public BigDecimal getHumedadSuelo() { return humedadSuelo; }
    public void setHumedadSuelo(BigDecimal humedadSuelo) { this.humedadSuelo = humedadSuelo; }

    public BigDecimal getRadiacionSolar() { return radiacionSolar; }
    public void setRadiacionSolar(BigDecimal radiacionSolar) { this.radiacionSolar = radiacionSolar; }

    public BigDecimal getIndiceUv() { return indiceUv; }
    public void setIndiceUv(BigDecimal indiceUv) { this.indiceUv = indiceUv; }

    public String getEstadoVentilacion() { return estadoVentilacion; }
    public void setEstadoVentilacion(String estadoVentilacion) { this.estadoVentilacion = estadoVentilacion; }
}