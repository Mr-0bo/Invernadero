package org.invernadero.repository;

import org.invernadero.model.AlertaActiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<AlertaActiva, Long> {

    // Spring Boot es tan inteligente que con solo nombrar así este método,
    // te devolverá automáticamente solo las alertas donde 'atendida' sea false.
    List<AlertaActiva> findByAtendidaFalse();
}