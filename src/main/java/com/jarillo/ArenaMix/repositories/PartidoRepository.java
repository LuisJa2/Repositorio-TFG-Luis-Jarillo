package com.jarillo.ArenaMix.repositories;
import com.jarillo.ArenaMix.models.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    void deleteByTorneo_Id(Integer torneoId);
}
