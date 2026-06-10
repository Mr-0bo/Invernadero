package org.invernadero.controller;

import org.invernadero.model.Zona;
import org.invernadero.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zonas")
@CrossOrigin(origins = "*") // Permite peticiones desde el frontend
public class ZonaController {

    @Autowired
    private ZonaRepository zonaRepository;

    @GetMapping
    public List<Zona> obtenerZonas() {
        // Devuelve todas las zonas en formato JSON
        return zonaRepository.findAll();
    }
}
