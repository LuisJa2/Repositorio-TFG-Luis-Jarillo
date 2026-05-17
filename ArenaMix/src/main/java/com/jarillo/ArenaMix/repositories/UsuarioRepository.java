package com.jarillo.ArenaMix.repositories;

import com.jarillo.ArenaMix.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // ¡Vacío! Spring Boot hace la magia por detrás.
}