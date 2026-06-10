package org.invernadero.repository;

import org.invernadero.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepository extends JpaRepository<Zona, Integer> {
    // Al extender JpaRepository, ya hereda métodos como save(), findById(), findAll(), etc.
}