package com.jarillo.ArenaMix.controllers;

import com.jarillo.ArenaMix.dto.ParticipanteDTO;
import com.jarillo.ArenaMix.excepciones.RecursoNoEncontradoException;
import com.jarillo.ArenaMix.models.Participante;
import com.jarillo.ArenaMix.models.Torneo;
import com.jarillo.ArenaMix.repositories.ParticipanteRepository;
import com.jarillo.ArenaMix.repositories.TorneoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/torneos/{torneoId}/participantes")
public class ParticipanteController {

    private final ParticipanteRepository participanteRepository;
    private final TorneoRepository torneoRepository;

    public ParticipanteController(ParticipanteRepository participanteRepository,
                                   TorneoRepository torneoRepository) {
        this.participanteRepository = participanteRepository;
        this.torneoRepository = torneoRepository;
    }

    @GetMapping
    public ResponseEntity<List<ParticipanteDTO>> listar(@PathVariable Integer torneoId) {
        List<ParticipanteDTO> lista = participanteRepository.findByTorneo_Id(torneoId)
                .stream()
                .map(p -> new ParticipanteDTO(p.getId(), p.getNombre(), torneoId))
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping
    public ResponseEntity<ParticipanteDTO> añadir(@PathVariable Integer torneoId,
                                                   @RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo no encontrado: " + torneoId));

        Participante p = new Participante();
        p.setTorneo(torneo);
        p.setNombre(nombre.trim());
        Participante guardado = participanteRepository.save(p);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ParticipanteDTO(guardado.getId(), guardado.getNombre(), torneoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer torneoId, @PathVariable Integer id) {
        Participante p = participanteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Participante no encontrado: " + id));
        participanteRepository.delete(p);
        return ResponseEntity.noContent().build();
    }
}
