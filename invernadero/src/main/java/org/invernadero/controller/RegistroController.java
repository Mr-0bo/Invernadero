package org.invernadero.controller;

import org.invernadero.model.AlertaActiva;
import org.invernadero.model.RegistroAmbiental;
import org.invernadero.repository.AlertaRepository;
import org.invernadero.repository.RegistroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registros")
@CrossOrigin(origins = "*")
public class RegistroController {

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private AlertaRepository alertaRepository; // <-- Inyectamos el control de alertas aquí también

    @GetMapping("/todos")
    public List<RegistroAmbiental> obtenerTodosLosRegistros() {
        return registroRepository.findAll();
    }

    @GetMapping("/ultimo/{idZona}")
    public RegistroAmbiental obtenerUltimoPorZona(@PathVariable Integer idZona) {
        return registroRepository.findTopByZonaIdZonaOrderByFechaHoraDesc(idZona)
                .orElse(null);
    }

    @GetMapping
    public List<RegistroAmbiental> filtrarRegistros(
            @RequestParam Integer idZona,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return registroRepository.findByZonaIdZonaAndFechaHoraBetweenOrderByFechaHoraDesc(idZona, inicio, fin);
    }

    @GetMapping("/estadisticas/hora")
    public List<Map<String, Object>> estadisticasPorHora(
            @RequestParam Integer idZona,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha) {
        return registroRepository.findEstadisticasPorHora(idZona, fecha);
    }

    @GetMapping("/estadisticas/dia")
    public List<Map<String, Object>> estadisticasPorDia(
            @RequestParam Integer idZona,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        return registroRepository.findEstadisticasPorDia(idZona, inicio, fin);
    }

    // --- AQUÍ ESTÁ LA MAGIA PARA EL MODO MANUAL ---
    @PostMapping
    public RegistroAmbiental crearRegistro(@RequestBody RegistroAmbiental registro) {
        // 1. Guardamos el registro manual
        RegistroAmbiental guardado = registroRepository.save(registro);

        // 2. Evaluamos si el dato manual rompe los límites
        if (guardado.getTemperaturaInterior() != null && guardado.getTemperaturaInterior().compareTo(new BigDecimal("35.0")) > 0) {
            generarAlerta(guardado, "PELIGRO TÉRMICO (MANUAL)", "La temperatura interior alcanzó " + guardado.getTemperaturaInterior() + "°C por inyección manual.");
        }

        if (guardado.getHumedadRelativa() != null && guardado.getHumedadRelativa().compareTo(new BigDecimal("30.0")) < 0) {
            generarAlerta(guardado, "ESTRÉS HÍDRICO (MANUAL)", "Humedad relativa crítica inyectada (" + guardado.getHumedadRelativa() + "%).");
        }

        return guardado;
    }

    // Método auxiliar para generar la alerta en BD
    private void generarAlerta(RegistroAmbiental registro, String tipo, String mensaje) {
        AlertaActiva alerta = new AlertaActiva();
        alerta.setRegistroAmbiental(registro);
        alerta.setTipoAlerta(tipo);
        alerta.setMensaje(mensaje);
        alerta.setAtendida(false);

        alertaRepository.save(alerta);
        System.out.println("⚠️ [ALERTA GENERADA] " + tipo + " en el endpoint POST");
    }
}