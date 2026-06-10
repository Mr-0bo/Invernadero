package org.invernadero.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Alertas_Activas")
public class AlertaActiva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    @ManyToOne
    @JoinColumn(name = "id_registro", nullable = false)
    private RegistroAmbiental registroAmbiental;

    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private String tipoAlerta;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_hora_generada", nullable = false, insertable = false, updatable = false)
    private LocalDateTime fechaHoraGenerada;

    @Column(nullable = false)
    private Boolean atendida = false;

    public AlertaActiva() {
    }

    public Long getIdAlerta() { return idAlerta; }
    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public RegistroAmbiental getRegistroAmbiental() { return registroAmbiental; }
    public void setRegistroAmbiental(RegistroAmbiental registroAmbiental) { this.registroAmbiental = registroAmbiental; }

    public String getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaHoraGenerada() { return fechaHoraGenerada; }
    public void setFechaHoraGenerada(LocalDateTime fechaHoraGenerada) { this.fechaHoraGenerada = fechaHoraGenerada; }

    public Boolean getAtendida() { return atendida; }
    public void setAtendida(Boolean atendida) { this.atendida = atendida; }
}