package org.invernadero.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.invernadero.model.AlertaActiva;
import org.invernadero.model.RegistroAmbiental;
import org.invernadero.model.Zona;
import org.invernadero.repository.AlertaRepository;
import org.invernadero.repository.RegistroRepository;
import org.invernadero.repository.ZonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class NasaApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RegistroRepository registroRepository;

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private AlertaRepository alertaRepository;

    @Scheduled(fixedRate = 3600000)
    // @Scheduled(fixedRate = 10000)
    public void descargarDatosNasa() {
        try {
            System.out.println("⏳ [NASA] Solicitando datos climáticos...");

            String latitud = "19";
            String longitud = "-99";

            // Tomamos la fecha de hace 10 días
            LocalDateTime ahora = LocalDateTime.now().minusDays(10);
            String fechaFormateada = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // ¡LA MAGIA AQUÍ! Creamos la llave exacta de la hora actual (Ej: "2026060418" para las 6 PM)
            String llaveHoraSincronizada = ahora.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));

            String url = String.format(
                    "https://power.larc.nasa.gov/api/temporal/hourly/point?parameters=T2M,RH2M,ALLSKY_SFC_SW_DWN&community=AG&longitude=%s&latitude=%s&format=JSON&start=%s&end=%s",
                    longitud, latitud, fechaFormateada, fechaFormateada
            );

            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root == null) return;

            JsonNode parameterNode = root.path("properties").path("parameter");

            // Ahora extraemos el valor específico de esta hora, no el último del día
            BigDecimal temperaturaExterior = obtenerValorPorHora(parameterNode.path("T2M"), llaveHoraSincronizada);
            BigDecimal humedadRelativa = obtenerValorPorHora(parameterNode.path("RH2M"), llaveHoraSincronizada);
            BigDecimal radiacionSolar = obtenerValorPorHora(parameterNode.path("ALLSKY_SFC_SW_DWN"), llaveHoraSincronizada);

            if (temperaturaExterior != null && humedadRelativa != null) {
                for (Zona zona : zonaRepository.findAll()) {

                    if (zona.getNombre() != null && zona.getNombre().equalsIgnoreCase("Zona Manual")) {
                        continue;
                    }

                    RegistroAmbiental registro = new RegistroAmbiental();
                    registro.setZona(zona);
                    registro.setTemperaturaExterior(temperaturaExterior);
                    registro.setHumedadRelativa(humedadRelativa);

                    // Si es de noche o no hay dato, guardamos 0
                    registro.setRadiacionSolar(radiacionSolar != null ? radiacionSolar : BigDecimal.ZERO);

                    BigDecimal tempInt = temperaturaExterior.add(new BigDecimal("3.5"));
                    registro.setTemperaturaInterior(tempInt);
                    registro.setHumedadSuelo(new BigDecimal("45.50"));
                    registro.setIndiceUv(new BigDecimal("5.2"));

                    if (tempInt.compareTo(new BigDecimal("30.0")) > 0) {
                        registro.setEstadoVentilacion("Encendido");
                    } else {
                        registro.setEstadoVentilacion("Apagado");
                    }

                    registroRepository.save(registro);
                    System.out.println("💾 [MySQL] Guardado [" + zona.getNombre() + "] | Temp: " + temperaturaExterior + "°C | Rad: " + registro.getRadiacionSolar());

                    // Motor de alertas
                    if (tempInt.compareTo(new BigDecimal("35.0")) > 0) {
                        generarAlerta(registro, "PELIGRO TÉRMICO", "La temperatura interior alcanzó " + tempInt + "°C. Riesgo de estrés térmico severo.");
                    }
                    if (humedadRelativa.compareTo(new BigDecimal("30.0")) < 0) {
                        generarAlerta(registro, "ESTRÉS HÍDRICO", "Humedad relativa crítica (" + humedadRelativa + "%). Aumente el riego.");
                    }
                }
            } else {
                System.out.println("⚠️ [NASA] No hay datos disponibles para la hora solicitada.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error en servicio NASA: " + e.getMessage());
        }
    }

    private void generarAlerta(RegistroAmbiental registro, String tipo, String mensaje) {
        AlertaActiva alerta = new AlertaActiva();
        alerta.setRegistroAmbiental(registro);
        alerta.setTipoAlerta(tipo);
        alerta.setMensaje(mensaje);
        alerta.setAtendida(false);
        alertaRepository.save(alerta);
    }

    // Nuevo método que busca exactamente la llave con la hora sincronizada
    private BigDecimal obtenerValorPorHora(JsonNode node, String llaveHora) {
        if (node != null && node.has(llaveHora)) {
            double val = node.get(llaveHora).asDouble();
            // La NASA devuelve -999 cuando no tiene el dato de radiación registrado, lo filtramos
            if (val > -900) {
                return BigDecimal.valueOf(val);
            }
        }
        return null;
    }
}