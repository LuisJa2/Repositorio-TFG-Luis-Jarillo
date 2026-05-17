package com.jarillo.ArenaMix.repositories;
import com.jarillo.ArenaMix.models.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Integer> {}