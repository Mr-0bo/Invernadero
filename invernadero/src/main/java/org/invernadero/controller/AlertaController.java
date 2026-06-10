package org.invernadero.controller;

import org.invernadero.model.AlertaActiva;
import org.invernadero.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    // Obtiene solo las alertas que están activas (no atendidas)
    @GetMapping
    public List<AlertaActiva> obtenerAlertasActivas() {
        return alertaRepository.findByAtendidaFalse();
    }

    // Este endpoint permitirá que un botón en el frontend "apague" la alerta
    @PutMapping("/{id}/resolver")
    public void resolverAlerta(@PathVariable Long id) {
        alertaRepository.findById(id).ifPresent(alerta -> {
            alerta.setAtendida(true);
            alertaRepository.save(alerta);
        });
    }
}
