package com.jarillo.ArenaMix.controllers;

import com.jarillo.ArenaMix.dto.PartidoDTO;
import com.jarillo.ArenaMix.dto.ResultadoDTO;
import com.jarillo.ArenaMix.excepciones.RecursoNoEncontradoException;
import com.jarillo.ArenaMix.models.Participante;
import com.jarillo.ArenaMix.models.Partido;
import com.jarillo.ArenaMix.models.Torneo;
import com.jarillo.ArenaMix.repositories.ParticipanteRepository;
import com.jarillo.ArenaMix.repositories.PartidoRepository;
import com.jarillo.ArenaMix.repositories.TorneoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class PartidoController {

    private final PartidoRepository partidoRepository;
    private final ParticipanteRepository participanteRepository;
    private final TorneoRepository torneoRepository;

    public PartidoController(PartidoRepository partidoRepository,
                              ParticipanteRepository participanteRepository,
                              TorneoRepository torneoRepository) {
        this.partidoRepository = partidoRepository;
        this.participanteRepository = participanteRepository;
        this.torneoRepository = torneoRepository;
    }

    // ── GET partidos de un torneo ──────────────────────────────────────────────
    @GetMapping("/api/torneos/{torneoId}/partidos")
    public ResponseEntity<List<PartidoDTO>> listar(@PathVariable Integer torneoId) {
        List<PartidoDTO> lista = partidoRepository.findByTorneo_IdOrderByRondaAsc(torneoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    // ── Generar partidos (ronda 1) ─────────────────────────────────────────────
    @PostMapping("/api/torneos/{torneoId}/partidos/generar")
    public ResponseEntity<?> generar(@PathVariable Integer torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo no encontrado: " + torneoId));

        List<Participante> participantes = participanteRepository.findByTorneo_Id(torneoId);
        if (participantes.size() < 2) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Se necesitan al menos 2 participantes."));
        }

        // Borrar partidos previos
        partidoRepository.deleteByTorneo_Id(torneoId);

        Collections.shuffle(participantes);
        String formato = torneo.getFormato() != null ? torneo.getFormato() : "TOURNAMENT";

        List<Partido> generados = new ArrayList<>();

        if ("LIGA".equals(formato) || "GROUPS_TOURNAMENT".equals(formato)) {
            // Todos contra todos
            for (int i = 0; i < participantes.size(); i++) {
                for (int j = i + 1; j < participantes.size(); j++) {
                    generados.add(crearPartido(torneo,
                            participantes.get(i).getNombre(),
                            participantes.get(j).getNombre(), 1));
                }
            }
        } else {
            // Eliminación directa — ronda 1
            for (int i = 0; i + 1 < participantes.size(); i += 2) {
                generados.add(crearPartido(torneo,
                        participantes.get(i).getNombre(),
                        participantes.get(i + 1).getNombre(), 1));
            }
        }

        partidoRepository.saveAll(generados);
        torneo.setEstado("EN_PROGRESO");
        torneoRepository.save(torneo);

        List<PartidoDTO> resultado = generados.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ── Siguiente ronda (solo TOURNAMENT) ─────────────────────────────────────
    @PostMapping("/api/torneos/{torneoId}/partidos/siguiente-ronda")
    public ResponseEntity<?> siguienteRonda(@PathVariable Integer torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo no encontrado: " + torneoId));

        List<Partido> todos = partidoRepository.findByTorneo_IdOrderByRondaAsc(torneoId);
        if (todos.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "No hay partidos generados."));
        }

        int maxRonda = todos.stream().mapToInt(Partido::getRonda).max().orElse(0);
        List<Partido> rondaActual = todos.stream()
                .filter(p -> p.getRonda() == maxRonda)
                .collect(Collectors.toList());

        boolean todosFin = rondaActual.stream().allMatch(p -> "FINALIZADO".equals(p.getEstado()));
        if (!todosFin) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Hay partidos pendientes en la ronda actual."));
        }

        List<String> ganadores = rondaActual.stream()
                .map(p -> p.getPuntosLocal() >= p.getPuntosVisitante()
                        ? p.getNombreLocal() : p.getNombreVisitante())
                .collect(Collectors.toList());

        if (ganadores.size() == 1) {
            return ResponseEntity.ok(Map.of("mensaje", "¡Torneo finalizado! Campeón: " + ganadores.get(0)));
        }

        List<Partido> nuevos = new ArrayList<>();
        for (int i = 0; i + 1 < ganadores.size(); i += 2) {
            nuevos.add(crearPartido(torneo, ganadores.get(i), ganadores.get(i + 1), maxRonda + 1));
        }
        partidoRepository.saveAll(nuevos);

        List<PartidoDTO> resultado = nuevos.stream().map(this::toDTO).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // ── Actualizar resultado ───────────────────────────────────────────────────
    @PutMapping("/api/partidos/{id}/resultado")
    public ResponseEntity<PartidoDTO> actualizarResultado(@PathVariable Integer id,
                                                           @RequestBody ResultadoDTO dto) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Partido no encontrado: " + id));
        partido.setPuntosLocal(dto.getPuntosLocal());
        partido.setPuntosVisitante(dto.getPuntosVisitante());
        partido.setEstado("FINALIZADO");
        return ResponseEntity.ok(toDTO(partidoRepository.save(partido)));
    }

    // ── Simular resultado aleatorio ────────────────────────────────────────────
    @PostMapping("/api/partidos/{id}/simular")
    public ResponseEntity<PartidoDTO> simular(@PathVariable Integer id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Partido no encontrado: " + id));

        Random rand = new Random();
        int pLocal = rand.nextInt(6);
        int pVisitante = rand.nextInt(6);
        // Evitar empate en eliminación
        String formato = partido.getTorneo().getFormato();
        if (pLocal == pVisitante && !"LIGA".equals(formato)) {
            pVisitante = (pVisitante + 1) % 6;
        }
        partido.setPuntosLocal(pLocal);
        partido.setPuntosVisitante(pVisitante);
        partido.setEstado("FINALIZADO");
        return ResponseEntity.ok(toDTO(partidoRepository.save(partido)));
    }

    // ── Generar fase eliminatoria desde grupos (GROUPS_TOURNAMENT) ────────────
    @PostMapping("/api/torneos/{torneoId}/partidos/generar-eliminacion")
    public ResponseEntity<?> generarEliminacion(@PathVariable Integer torneoId) {
        Torneo torneo = torneoRepository.findById(torneoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Torneo no encontrado: " + torneoId));

        List<Partido> todos = partidoRepository.findByTorneo_IdOrderByRondaAsc(torneoId);

        List<Partido> gruposPartidos = todos.stream()
                .filter(p -> p.getRonda() == 1)
                .collect(Collectors.toList());

        if (gruposPartidos.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "No hay partidos de grupos generados."));
        }

        boolean allDone = gruposPartidos.stream().allMatch(p -> "FINALIZADO".equals(p.getEstado()));
        if (!allDone) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "Hay partidos de grupos pendientes de finalizar."));
        }

        boolean hasElim = todos.stream().anyMatch(p -> p.getRonda() > 1);
        if (hasElim) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", "La fase eliminatoria ya ha sido generada."));
        }

        // Calcular clasificación: [pts, gd, gf]
        Map<String, int[]> standings = new java.util.LinkedHashMap<>();
        for (Partido p : gruposPartidos) {
            standings.putIfAbsent(p.getNombreLocal(),    new int[]{0, 0, 0});
            standings.putIfAbsent(p.getNombreVisitante(), new int[]{0, 0, 0});
            int[] l = standings.get(p.getNombreLocal());
            int[] v = standings.get(p.getNombreVisitante());
            l[2] += p.getPuntosLocal();
            l[1] += p.getPuntosLocal() - p.getPuntosVisitante();
            v[2] += p.getPuntosVisitante();
            v[1] += p.getPuntosVisitante() - p.getPuntosLocal();
            if (p.getPuntosLocal() > p.getPuntosVisitante())       { l[0] += 3; }
            else if (p.getPuntosLocal() < p.getPuntosVisitante())  { v[0] += 3; }
            else                                                    { l[0]++; v[0]++; }
        }

        // Ordenar por pts desc, gd desc, gf desc
        List<String> sorted = new ArrayList<>(standings.keySet());
        sorted.sort((a, b) -> {
            int[] sa = standings.get(a), sb = standings.get(b);
            if (sb[0] != sa[0]) return sb[0] - sa[0];
            if (sb[1] != sa[1]) return sb[1] - sa[1];
            return sb[2] - sa[2];
        });

        // Top 60%, mínimo 2 y número par (redondear hacia arriba si es impar)
        int n = (int) Math.floor(sorted.size() * 0.6);
        if (n < 2) n = 2;
        if (n % 2 != 0) n = Math.min(n + 1, sorted.size());
        n = Math.min(n, sorted.size());

        List<String> clasificados = sorted.subList(0, n);

        List<Partido> nuevos = new ArrayList<>();
        for (int i = 0; i + 1 < clasificados.size(); i += 2) {
            nuevos.add(crearPartido(torneo, clasificados.get(i), clasificados.get(i + 1), 2));
        }
        partidoRepository.saveAll(nuevos);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(nuevos.stream().map(this::toDTO).collect(Collectors.toList()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Partido crearPartido(Torneo torneo, String nombreLocal, String nombreVisitante, int ronda) {
        Partido p = new Partido();
        p.setTorneo(torneo);
        p.setNombreLocal(nombreLocal);
        p.setNombreVisitante(nombreVisitante);
        p.setRonda(ronda);
        p.setEstado("PENDIENTE");
        p.setPuntosLocal(0);
        p.setPuntosVisitante(0);
        return p;
    }

    private PartidoDTO toDTO(Partido p) {
        return new PartidoDTO(p.getId(), p.getNombreLocal(), p.getNombreVisitante(),
                p.getPuntosLocal(), p.getPuntosVisitante(), p.getRonda(), p.getEstado());
    }
}
