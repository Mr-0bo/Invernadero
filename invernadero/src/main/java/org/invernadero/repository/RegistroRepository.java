package org.invernadero.repository;

import org.invernadero.model.RegistroAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface RegistroRepository extends JpaRepository<RegistroAmbiental, Long> {

    // --- NUEVO: Obtener el registro más reciente de una zona ---
    Optional<RegistroAmbiental> findTopByZonaIdZonaOrderByFechaHoraDesc(Integer idZona);

    // 1. Filtrar historial por zona y rango de fechas
    List<RegistroAmbiental> findByZonaIdZonaAndFechaHoraBetweenOrderByFechaHoraDesc(
            Integer idZona, LocalDateTime inicio, LocalDateTime fin);

    // 2. Estadísticas agrupadas por hora para un día específico y una zona
    @Query(value = "SELECT HOUR(fecha_hora) as hora, " +
            "AVG(temperatura_interior) as avgTempInt, " +
            "AVG(temperatura_exterior) as avgTempExt, " +
            "AVG(humedad_relativa) as avgHumAire, " +
            "AVG(humedad_suelo) as avgHumSuelo " +
            "FROM Registros_Ambientales " +
            "WHERE id_zona = :idZona AND DATE(fecha_hora) = DATE(:fecha) " +
            "GROUP BY HOUR(fecha_hora) " +
            "ORDER BY hora ASC", nativeQuery = true)
    List<Map<String, Object>> findEstadisticasPorHora(@Param("idZona") Integer idZona, @Param("fecha") LocalDateTime fecha);

    // 3. Estadísticas agrupadas por día para un rango de fechas y una zona
    @Query(value = "SELECT DATE(fecha_hora) as fecha, " +
            "AVG(temperatura_interior) as avgTempInt, " +
            "AVG(temperatura_exterior) as avgTempExt, " +
            "AVG(humedad_relativa) as avgHumAire, " +
            "AVG(humedad_suelo) as avgHumSuelo " +
            "FROM Registros_Ambientales " +
            "WHERE id_zona = :idZona AND fecha_hora BETWEEN :inicio AND :fin " +
            "GROUP BY DATE(fecha_hora) " +
            "ORDER BY fecha ASC", nativeQuery = true)
    List<Map<String, Object>> findEstadisticasPorDia(@Param("idZona") Integer idZona,
                                                     @Param("inicio") LocalDateTime inicio,
                                                     @Param("fin") LocalDateTime fin);
}