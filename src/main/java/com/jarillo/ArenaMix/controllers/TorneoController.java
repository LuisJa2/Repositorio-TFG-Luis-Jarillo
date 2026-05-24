package com.jarillo.ArenaMix.controllers;

import com.jarillo.ArenaMix.dto.TorneoCreateDTO;
import com.jarillo.ArenaMix.dto.TorneoResponseDTO;
import com.jarillo.ArenaMix.services.TorneoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
@Tag(name = "Torneos", description = "Gestión de torneos deportivos")
public class TorneoController {

    private final TorneoService torneoService;

    public TorneoController(TorneoService torneoService) {
        this.torneoService = torneoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los torneos", description = "Devuelve todos los torneos disponibles. No requiere autenticación.")
    @ApiResponse(responseCode = "200", description = "Lista de torneos")
    public ResponseEntity<List<TorneoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(torneoService.obtenerTodos());
    }

    @GetMapping("/mis-torneos")
    @Operation(summary = "Mis torneos", description = "Devuelve los torneos organizados por el usuario autenticado.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de torneos del usuario"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<TorneoResponseDTO>> getMisTorneos(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(torneoService.obtenerPorOrganizador(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener torneo por ID", description = "Devuelve los detalles de un torneo concreto. No requiere autenticación.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<TorneoResponseDTO> obtenerPorId(
            @Parameter(description = "ID del torneo") @PathVariable Integer id) {
        return ResponseEntity.ok(torneoService.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear torneo", description = "Crea un nuevo torneo. Requiere autenticación.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Torneo creado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<TorneoResponseDTO> crearTorneo(@RequestBody TorneoCreateDTO dto) {
        TorneoResponseDTO torneoCreado = torneoService.crearTorneo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(torneoCreado);
    }

    @PatchMapping("/{id}/finalizar")
    @Operation(summary = "Finalizar torneo", description = "Cambia el estado del torneo a FINALIZADO. Requiere autenticación.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torneo marcado como finalizado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<TorneoResponseDTO> finalizarTorneo(
            @Parameter(description = "ID del torneo a finalizar") @PathVariable Integer id) {
        return ResponseEntity.ok(torneoService.finalizarTorneo(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar torneo", description = "Elimina un torneo y todos sus datos asociados. Requiere autenticación.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Torneo eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<Void> eliminarTorneo(
            @Parameter(description = "ID del torneo a eliminar") @PathVariable Integer id) {
        torneoService.eliminarTorneo(id);
        return ResponseEntity.noContent().build();
    }
}
