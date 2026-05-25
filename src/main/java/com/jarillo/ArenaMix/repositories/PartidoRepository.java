package com.jarillo.ArenaMix.repositories;
import com.jarillo.ArenaMix.models.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Integer> {

    java.util.List<Partido> findByTorneo_IdOrderByRondaAsc(Integer torneoId);
    void deleteByTorneo_Id(Integer torneoId);
}
