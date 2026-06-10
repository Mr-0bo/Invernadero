package org.invernadero.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Reglas_Negocio")
public class ReglaNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regla")
    private Integer idRegla;

    @Column(name = "nombre_variable", nullable = false, unique = true, length = 50)
    private String nombreVariable;

    @Column(name = "valor_limite", nullable = false, precision = 6, scale = 2)
    private BigDecimal valorLimite;

    @Column(nullable = false, length = 255)
    private String descripcion;

    public ReglaNegocio() {
    }

    public Integer getIdRegla() { return idRegla; }
    public void setIdRegla(Integer idRegla) { this.idRegla = idRegla; }

    public String getNombreVariable() { return nombreVariable; }
    public void setNombreVariable(String nombreVariable) { this.nombreVariable = nombreVariable; }

    public BigDecimal getValorLimite() { return valorLimite; }
    public void setValorLimite(BigDecimal valorLimite) { this.valorLimite = valorLimite; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}