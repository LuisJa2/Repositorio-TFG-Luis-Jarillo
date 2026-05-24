package com.jarillo.ArenaMix.repositories;
import com.jarillo.ArenaMix.models.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {

    List<Torneo> findByOrganizador_Email(String email);
}