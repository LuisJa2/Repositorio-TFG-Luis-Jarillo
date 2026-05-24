package com.jarillo.ArenaMix.services;

import com.jarillo.ArenaMix.dto.TorneoCreateDTO;
import com.jarillo.ArenaMix.dto.TorneoResponseDTO;
import com.jarillo.ArenaMix.excepciones.RecursoNoEncontradoException;
import com.jarillo.ArenaMix.models.Torneo;
import com.jarillo.ArenaMix.models.Usuario;
import com.jarillo.ArenaMix.repositories.ParticipanteRepository;
import com.jarillo.ArenaMix.repositories.PartidoRepository;
import com.jarillo.ArenaMix.repositories.TorneoRepository;
import com.jarillo.ArenaMix.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TorneoService {

    private final TorneoRepository torneoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ParticipanteRepository participanteRepository;
    private final PartidoRepository partidoRepository;

    public TorneoService(TorneoRepository torneoRepository,
                         UsuarioRepository usuarioRepository,
                         ParticipanteRepository participanteRepository,
                         PartidoRepository partidoRepository) {
        this.torneoRepository = torneoRepository;
        this.usuarioRepository = usuarioRepository;
        this.participanteRepository = participanteRepository;
        this.partidoRepository = partidoRepository;
    }

    @Transactional(readOnly = true)
    public List<TorneoResponseDTO> obtenerTodos() {
        return torneoRepository.findAll()
                .stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TorneoResponseDTO obtenerPorId(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Torneo no encontrado con ID: " + id));
        return mapearAResponseDTO(torneo);
    }

    @Transactional(readOnly = true)
    public List<TorneoResponseDTO> obtenerPorOrganizador(String email) {
        return torneoRepository.findByOrganizador_Email(email)
                .stream()
                .map(this::mapearAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TorneoResponseDTO crearTorneo(TorneoCreateDTO dto) {
        Usuario organizador = usuarioRepository.findById(dto.getOrganizadorId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario organizador no encontrado con ID: " + dto.getOrganizadorId()));

        Torneo torneo = new Torneo();
        torneo.setNombre(dto.getNombre());
        torneo.setDeporte(dto.getDeporte());
        torneo.setOrganizador(organizador);
        torneo.setFormato(dto.getFormato() != null ? dto.getFormato() : "TOURNAMENT");

        Torneo torneoGuardado = torneoRepository.save(torneo);

        return mapearAResponseDTO(
                torneoRepository.findById(torneoGuardado.getId()).orElseThrow());
    }

    @Transactional
    public TorneoResponseDTO finalizarTorneo(Integer id) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Torneo no encontrado con ID: " + id));
        torneo.setEstado("FINALIZADO");
        return mapearAResponseDTO(torneoRepository.save(torneo));
    }

    @Transactional
    public void eliminarTorneo(Integer id) {
        if (!torneoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Torneo no encontrado con ID: " + id);
        }
        // Eliminar dependencias en cascada manual para respetar las FK
        partidoRepository.deleteByTorneo_Id(id);
        participanteRepository.deleteByTorneo_Id(id);
        torneoRepository.deleteById(id);
    }

    private TorneoResponseDTO mapearAResponseDTO(Torneo torneo) {
        return new TorneoResponseDTO(
                torneo.getId(),
                torneo.getNombre(),
                torneo.getDeporte(),
                torneo.getOrganizador().getUsername(),
                torneo.getEstado(),
                torneo.getFechaCreacion(),
                torneo.getFormato()
        );
    }
}
