package itson.dominiorummy.facade;

import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.FichaPlaced;
import itson.dominiorummy.entidades.Grupo;
import itson.dominiorummy.entidades.GrupoNumero;
import itson.dominiorummy.entidades.GrupoSecuencia;
import itson.dominiorummy.entidades.Jugador;
import itson.dominiorummy.entidades.Sopa;
import itson.dominiorummy.entidades.Tablero;
import itson.dominiorummy.entidades.Turno;
import itson.dominiorummy.mappers.FichaMapper;
import itson.dominiorummy.mappers.TableroMapper;
import itson.producerdominio.facade.IProducerDominio;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.JugadorDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Dominio implements IDominio {

    private final Tablero tablero;
    private final IProducerDominio producer;
    private final Turno turno;
    private final Sopa sopa;
    private final List<Ficha> fichas;
    private final Map<String, Jugador> jugadores;

    private static final Logger LOG = Logger.getLogger(Dominio.class.getName());

    public Dominio(Tablero tablero, IProducerDominio producer, Turno turno, Sopa sopa, List<Ficha> fichas) {
        this.fichas = fichas;
        this.tablero = tablero;
        this.producer = producer;
        this.turno = turno;
        this.jugadores = new HashMap<>();
        this.sopa = sopa;
    }

    // INICIAR PARTIDA
    public void iniciarPartida() {
        try {
            for (Jugador j : this.jugadores.values()) {
                producer.actualizarManoJugador(
                        j.getId(),
                        FichaMapper.toDTO(j.getMano().getFichas())
                );
                System.out.println("[DOMINIO] Mano inicial enviada a: " + j.getNombre());
            }

            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarSopa(sopa.getFichasRestantes());

            Jugador jugadorEnTurno = turno.getJugadorActual();
            if (jugadorEnTurno == null) {
                LOG.warning("No hay jugador actual definido al iniciar la partida.");
                return;
            }

            String jugadorIdTurno = jugadorEnTurno.getId();
            producer.actualizarTurno(jugadorIdTurno);

        } catch (Exception e) {
            LOG.severe("Error al iniciar la partida: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // CREAR GRUPO
    @Override
    public void crearGrupo(List<FichaDTO> fichasDTO) {
        Jugador jugador = turno.getJugadorActual();
        String jugadorId = jugador.getId();
        int turnoActual = turno.getNumeroTurno();

        List<FichaPlaced> fichasAInsertar = new ArrayList<>();
        List<String> idsSacadosDeMano = new ArrayList<>();

        if (!recolectarFichas(fichasDTO, jugador, turnoActual, fichasAInsertar, idsSacadosDeMano)) {
            return;
        }

        Grupo grupoNuevo = tablero.crearGrupoDesdeFichasPlaced(fichasAInsertar);

        if (grupoNuevo == null || !grupoNuevo.validarReglas()) {
            rollbackCreacion(fichasAInsertar, idsSacadosDeMano, jugador);
            producer.mostrarError(jugadorId, "Movimiento inválido: Las fichas no forman una estructura correcta.");
            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarManoJugador(jugadorId, FichaMapper.toDTO(jugador.getMano().getFichas()));
            return;
        }

        tablero.agregarGrupo(grupoNuevo);
        producer.actualizarTablero(TableroMapper.toDTO(tablero));
        producer.actualizarManoJugador(jugadorId, FichaMapper.toDTO(jugador.getMano().getFichas()));
        producer.enviarCantidadFichasPublico(jugadorId, jugador.getMano().getFichas().size());
    }

    // ACTUALIZAR GRUPO
    @Override
    public void actualizarGrupo(String grupoId, List<FichaDTO> fichasEntrantesDTO) {

        Jugador jugador = turno.getJugadorActual();
        String jugadorId = jugador.getId();
        int turnoActual = turno.getNumeroTurno();

        List<FichaPlaced> fichasExistentes = tablero.obtenerFichasDeGrupo(grupoId);

        Set<String> idsExistentes = fichasExistentes.stream()
                .map(fp -> fp.getFicha().getId())
                .collect(Collectors.toSet());

        List<FichaDTO> fichasRealmenteNuevas = new ArrayList<>();

        for (FichaDTO dto : fichasEntrantesDTO) {
            if (!idsExistentes.contains(dto.getId())) {
                fichasRealmenteNuevas.add(dto);
            }
        }

        if (fichasRealmenteNuevas.isEmpty()) {
            return;
        }

        List<FichaPlaced> combinacionPrueba = fichasExistentes.stream()
                .map(FichaPlaced::clonar)
                .collect(Collectors.toList());

        List<FichaPlaced> fichasNuevasInsertar = new ArrayList<>();
        List<String> idsSacadosDeMano = new ArrayList<>();

        if (!recolectarFichas(fichasRealmenteNuevas, jugador, turnoActual, fichasNuevasInsertar, idsSacadosDeMano)) {
            return;
        }


        combinacionPrueba.addAll(fichasNuevasInsertar);

        Grupo grupoResultante = tablero.crearGrupoDesdeFichasPlaced(combinacionPrueba);
        boolean esValido = (grupoResultante != null && grupoResultante.validarReglas());

        if (esValido) {
            Grupo grupoFinal = forzarIdGrupo(grupoResultante, grupoId);
            tablero.removerGrupo(grupoId);
            tablero.agregarGrupo(grupoFinal);
        } else {
            producer.mostrarError(jugadorId, "La ficha no encaja. Se creó un nuevo grupo.");

            Grupo grupoRebote = tablero.crearGrupoDesdeFichasPlaced(fichasNuevasInsertar);
            if (grupoRebote != null) {
                tablero.agregarGrupo(grupoRebote);
            }
        }

        if (tablero.grupoEstaVacio(grupoId)) {
            tablero.removerGrupo(grupoId);
        }

        producer.actualizarTablero(TableroMapper.toDTO(tablero));
        producer.actualizarManoJugador(jugadorId, FichaMapper.toDTO(jugador.getMano().getFichas()));
        producer.enviarCantidadFichasPublico(jugadorId, jugador.getMano().getFichas().size());
    }

    private boolean recolectarFichas(List<FichaDTO> dtos, Jugador jugador, int turnoActual,
            List<FichaPlaced> destino, List<String> idsMano) {
        for (FichaDTO dto : dtos) {
            String fid = dto.getId();
            if (jugador.getMano().tieneFicha(fid)) {
                Ficha f = jugador.getMano().quitarFicha(fid);
                idsMano.add(fid);
                destino.add(new FichaPlaced(f, jugador.getId(), turnoActual));
                continue;
            }
            FichaPlaced fp = tablero.buscarFichaPlacedGlobal(fid);
            if (fp != null) {
                tablero.removerFichaGlobal(fid);
                destino.add(fp);
                continue;
            }
            producer.mostrarError(jugador.getId(), "Ficha no encontrada: " + fid);
            rollbackCreacion(destino, idsMano, jugador);
            return false;
        }
        return true;
    }

    private void rollbackCreacion(List<FichaPlaced> fichas, List<String> idsMano, Jugador jugador) {
        for (FichaPlaced fp : fichas) {
            if (idsMano.contains(fp.getFicha().getId())) {
                jugador.getMano().agregarFicha(fp.getFicha());
            } else {
                tablero.restaurarFicha(fp);
            }
        }
    }

    private Grupo forzarIdGrupo(Grupo g, String id) {
        if (g instanceof GrupoNumero) {
            return new GrupoNumero(id, g.getFichas());
        }
        return new GrupoSecuencia(id, g.getFichas());
    }

    // TOMAR FICHA
    @Override
    public void tomarFicha() {
        Jugador jugador = turno.getJugadorActual();
        String jugadorId = jugador.getId();
        int turnoActual = turno.getNumeroTurno();

        if (jugador == null) {
            producer.mostrarError(jugadorId, "Jugador no encontrado.");
            return;
        }

        tablero.revertirJugadasDelTurno(jugadorId, turnoActual, jugador.getMano());

        Ficha ficha = sopa.tomarFicha();
        if (ficha != null) {
            jugador.getMano().agregarFicha(ficha);
            producer.actualizarSopa(sopa.getFichasRestantes());
        } else {
            producer.mostrarError(jugadorId, "La sopa está vacía.");
        }

        jugador.setHaTomadoFicha(true);

        List<Ficha> fichasJugador = jugador.getMano().getFichas();
        producer.actualizarManoJugador(jugadorId, FichaMapper.toDTO(fichasJugador));
        producer.actualizarTablero(TableroMapper.toDTO(tablero));
        producer.enviarCantidadFichasPublico(jugadorId, fichasJugador.size());

        terminarTurno();
    }

    // AGREGAR JUGADOR
    @Override
    public void agregarJugador(Jugador jugador) {
        jugadores.put(jugador.getId(), jugador);
    }

    // TERMINAR TURNO
    @Override
    public void terminarTurno() {
        Jugador jugador = turno.getJugadorActual();
        String jugadorId = jugador.getId();
        int numeroTurnoActual = turno.getNumeroTurno();

        if (jugador == null) {
            producer.mostrarError(jugadorId, "Jugador actual no definido.");
            return;
        }

        try {
            Jugador jugadorActual = jugadores.get(jugadorId);
            if (jugadorActual == null) {
                producer.mostrarError(jugadorId, "Jugador no encontrado.");
                return;
            }

            // 1. Validar grupos < 3 fichas
            for (Grupo grupo : tablero.getGruposEnMesa()) {
                if (!grupo.cumpleTamanoMinimo()) {
                    producer.mostrarError(jugadorId, "No puedes terminar: Hay grupos con menos de 3 fichas.");
                    producer.highlightInvalidGroup(jugadorId, grupo.getId());
                    return; // Aquí retorna y NO pasa turno. Correcto.
                }
            }

            int puntosDeBajada = 0;
            boolean huboMovimientoEnTablero = false;

            // 2. Validar reglas de 30 pts y detectar actividad
            for (Grupo grupo : tablero.getGruposEnMesa()) {
                if (!jugador.yaBajo30() && grupo.contieneFichasAntiguas(numeroTurnoActual)) {
                    boolean tieneNuevas = grupo.getFichas().stream()
                            .anyMatch(fp -> fp.getPlacedInTurn() == numeroTurnoActual);
                    if (tieneNuevas) {
                        producer.mostrarError(jugadorId, "No puedes manipular grupos ajenos hasta bajar tus 30 pts.");
                        return;
                    }
                }

                if (grupo.fueCreadoEnTurno(numeroTurnoActual, jugadorId)) {
                    puntosDeBajada += grupo.calcularPuntos();
                    huboMovimientoEnTablero = true;
                } else {
                    boolean tieneNuevas = grupo.getFichas().stream()
                            .anyMatch(fp -> fp.getPlacedInTurn() == numeroTurnoActual && fp.getPlacedBy().equals(jugadorId));
                    if (tieneNuevas) {
                        huboMovimientoEnTablero = true;
                    }
                }
            }

            if (!jugador.yaBajo30()) {
                if (huboMovimientoEnTablero && puntosDeBajada < 30) {
                    producer.mostrarError(jugadorId, "Primera bajada insuficiente (" + puntosDeBajada + "/30 pts).");
                    return;
                }
                if (huboMovimientoEnTablero && puntosDeBajada >= 30) {
                    jugador.marcarPrimerBajada30Completada();
                }
            }

            // 3. Validar inactividad (Si no movió nada, debe haber comido)
            if (!huboMovimientoEnTablero) {
                if (!jugadorActual.haTomadoFicha()) {
                    producer.mostrarError(jugadorId, "No puedes pasar turno sin hacer nada. Baja un juego o toma una ficha.");
                    return;
                }
            }

            // 4. Confirmar y Avanzar
            tablero.marcarFichasConfirmadas(jugadorId);

            if (jugadorActual.getMano().getFichas().isEmpty()) {
                JugadorDTO ganadorDTO = new JugadorDTO();
                ganadorDTO.setId(jugadorActual.getId());
                ganadorDTO.setNombre(jugadorActual.getNombre());
                ganadorDTO.setAvatarPath("");

                producer.juegoTerminado(ganadorDTO);
                return;
            }

            Jugador siguienteJugador = turno.avanzarTurno();
            if (siguienteJugador == null) {
                producer.mostrarError(jugadorId, "No se pudo determinar el siguiente jugador.");
                return;
            }

            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarTurno(siguienteJugador.getId());

        } catch (Exception e) {
            LOG.severe("Error al terminar turno: " + e.getMessage());
            producer.mostrarError(jugadorId, "Ocurrió un error inesperado.");
        }
    }
}
