package com.jarillo.ArenaMix;

import com.jarillo.ArenaMix.models.*;
import com.jarillo.ArenaMix.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ArenaMixApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArenaMixApplication.class, args);
	}

	// Inyectamos todos los repositorios que acabas de crear
	@Bean
	public CommandLineRunner probarBaseDeDatosCompleta(
			UsuarioRepository usuarioRepo,
			TorneoRepository torneoRepo,
			ParticipanteRepository participanteRepo,
			PartidoRepository partidoRepo) {

		return args -> {
			System.out.println("=== INICIANDO PRUEBA COMPLETA DE RELACIONES EN AWS ===");

			// 1. Recuperar al Organizador que creamos antes o crearlo si no existe
			Usuario organizador = usuarioRepo.findAll().stream()
					.filter(u -> u.getUsername().equals("AdminTest"))
					.findFirst()
					.orElseGet(() -> {
						Usuario nuevo = new Usuario("AdminTest", "admin@test.com", "secreta123", "ORGANIZADOR");
						return usuarioRepo.save(nuevo);
					});

			// 2. Crear un segundo Usuario (Un jugador de prueba)
			Usuario jugador = usuarioRepo.findAll().stream()
					.filter(u -> u.getUsername().equals("JugadorPro"))
					.findFirst()
					.orElseGet(() -> {
						Usuario nuevo = new Usuario("JugadorPro", "jugador@test.com", "1234", "JUGADOR");
						return usuarioRepo.save(nuevo);
					});

			// 3. Crear un Torneo solo si la tabla está vacía
			if (torneoRepo.count() == 0) {
				System.out.println("Creando Torneo de prueba...");
				Torneo torneo = new Torneo();
				torneo.setNombre("Gran Torneo Inaugural ArenaMix");
				torneo.setDeporte("eSports - Valorant");
				torneo.setOrganizador(organizador); // ¡Aquí aplicamos la Foreign Key!
				torneo = torneoRepo.save(torneo);

				System.out.println("Inscribiendo participantes...");
				// 4. Inscribir a los dos usuarios en el torneo
				Participante inscripcion1 = new Participante();
				inscripcion1.setTorneo(torneo);
				inscripcion1.setUsuario(organizador); // El admin también juega
				participanteRepo.save(inscripcion1);

				Participante inscripcion2 = new Participante();
				inscripcion2.setTorneo(torneo);
				inscripcion2.setUsuario(jugador);
				participanteRepo.save(inscripcion2);

				System.out.println("Creando primer partido...");
				// 5. Crear el Partido (Enfrentamiento entre los dos)
				Partido partido = new Partido();
				partido.setTorneo(torneo);
				partido.setLocal(organizador);
				partido.setVisitante(jugador);
				partido.setRonda(1); // Final directa
				partidoRepo.save(partido);

				System.out.println("¡Datos de prueba insertados con éxito en todas las tablas!");
			} else {
				System.out.println("La base de datos ya tiene torneos, omitiendo la prueba.");
			}

			System.out.println("======================================================");
		};
	}
}