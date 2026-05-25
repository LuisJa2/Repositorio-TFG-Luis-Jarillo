package com.jarillo.ArenaMix.repositories;
import com.jarillo.ArenaMix.models.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipanteRepository extends JpaRepository<Participante, Integer> {

    java.util.List<Participante> findByTorneo_Id(Integer torneoId);
    void deleteByTorneo_Id(Integer torneoId);
}