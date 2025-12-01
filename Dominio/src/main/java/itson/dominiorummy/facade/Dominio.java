/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.facade;

import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.FichaPlaced;
import itson.dominiorummy.entidades.Grupo;
import itson.dominiorummy.entidades.GrupoNumero;
import itson.dominiorummy.entidades.GrupoSecuencia;
import itson.dominiorummy.entidades.Jugador;
import itson.dominiorummy.entidades.Mano;
import itson.dominiorummy.entidades.Sopa;
import itson.dominiorummy.entidades.Tablero;
import itson.dominiorummy.entidades.Turno;
import itson.dominiorummy.mappers.FichaMapper;
import itson.dominiorummy.mappers.TableroMapper;
import itson.producerdominio.facade.IProducerDominio;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Dana Chavez
 */
public class Dominio implements IDominio {

    private final Tablero tablero;
    private final IProducerDominio producer;
    private final Turno turno;
    private final Sopa sopa;
    private final List<Ficha> fichas;
    private final Map<String, Jugador> jugadores;
    private static final Logger LOG = Logger.getLogger(itson.dominiorummy.facade.Dominio.class.getName());

    public Dominio(Tablero tablero, IProducerDominio producer, Turno turno, Sopa sopa, List<Ficha> ficha) {
        this.fichas = ficha;
        this.tablero = tablero;
        this.producer = producer;
        this.turno = turno;
        this.jugadores = new HashMap<>();
        this.sopa = new Sopa(fichas);
    }

    public void iniciarPartida() {
        try {
            Jugador jugador = turno.getJugadorActual();
            if (jugador == null) {
                LOG.warning("No hay jugador actual definido al iniciar la partida.");
                return;
            }

            String jugadorIdInterno = jugador.getId();

            String jugadorIdPublico = jugador.getNombre();

            producer.actualizarManoJugador(
                    FichaMapper.toDTO(jugador.getMano().getFichas())
            );

            TableroDTO tableroActualizado = FichaMapper.toDTO(tablero);
            producer.actualizarTablero(tableroActualizado);

            producer.actualizarTurno(jugadorIdPublico);

        } catch (Exception e) {
            LOG.severe("Error al iniciar la partida: " + e.getMessage());
            producer.mostrarError("Ocurrió un error al iniciar la partida.");
        }
    }

    @Override
    public void crearGrupo(List<FichaDTO> fichasDTO) {

        Jugador jugador = turno.getJugadorActual();
        Mano mano = jugador.getMano();
        int turnoActual = turno.getNumeroTurno();

        List<FichaPlaced> fichasAInsertar = new ArrayList<>();

        List<String> idsSacadosDeMano = new ArrayList<>();

        for (FichaDTO dto : fichasDTO) {
            String fichaId = dto.getId();

            if (mano.tieneFicha(fichaId)) {
                Ficha base = mano.quitarFicha(fichaId);
                idsSacadosDeMano.add(fichaId);

                FichaPlaced fp = new FichaPlaced(base, jugador.getId(), turnoActual);
                fichasAInsertar.add(fp);
                continue;
            }

            FichaPlaced fpDesdeTablero = tablero.buscarFichaPlacedGlobal(fichaId);

            if (fpDesdeTablero != null) {
                tablero.removerFichaGlobal(fichaId);
                fichasAInsertar.add(fpDesdeTablero);
                continue;
            }

            producer.mostrarError("La ficha " + fichaId + " no está disponible.");
            rollbackCreacion(fichasAInsertar, idsSacadosDeMano, jugador);
            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarManoJugador(FichaMapper.toDTO(jugador.getMano().getFichas()));
            return;
        }

        Grupo grupoNuevo = tablero.crearGrupoDesdeFichasPlaced(fichasAInsertar);

        if (grupoNuevo == null) {
            rollbackCreacion(fichasAInsertar, idsSacadosDeMano, jugador);

            producer.mostrarError("Las fichas no forman un grupo válido.");

            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarManoJugador(FichaMapper.toDTO(jugador.getMano().getFichas()));
            return;
        }
        tablero.agregarGrupo(grupoNuevo);

        producer.actualizarTablero(TableroMapper.toDTO(tablero));
        producer.actualizarManoJugador(FichaMapper.toDTO(jugador.getMano().getFichas()));
    }

    private void rollbackCreacion(List<FichaPlaced> fichasProcesadas, List<String> idsDeMano, Jugador jugador) {
        for (FichaPlaced fp : fichasProcesadas) {
            String id = fp.getFicha().getId();
            if (idsDeMano.contains(id)) {
                jugador.getMano().agregarFicha(fp.getFicha());
            } else {
                tablero.restaurarFicha(fp);
            }
        }
    }

    @Override
    public void actualizarGrupo(String grupoId, List<FichaDTO> fichasNuevasDTO) {

        Jugador jugador = turno.getJugadorActual();
        Mano mano = jugador.getMano();
        int turnoActual = turno.getNumeroTurno();

        List<FichaPlaced> estadoOriginalGrupo = tablero.obtenerFichasDeGrupo(grupoId)
                .stream()
                .map(FichaPlaced::clonar)
                .collect(Collectors.toList());

        List<FichaPlaced> fichasAntes = tablero.obtenerFichasDeGrupo(grupoId);
        List<Ficha> manoAntes = mano.clonarContenido();

        tablero.limpiarGrupo(grupoId);

        List<FichaPlaced> fichasInsertadas = new ArrayList<>();

        for (FichaDTO dto : fichasNuevasDTO) {
            String fichaId = dto.getId();

            FichaPlaced existente = fichasAntes.stream()
                    .filter(fp -> fp.getFicha().getId().equals(fichaId)).findFirst().orElse(null);
            if (existente != null) {
                tablero.agregarFichaAGrupo(grupoId, existente);
                fichasInsertadas.add(existente);
                continue;
            }

            if (mano.tieneFicha(fichaId)) {
                Ficha base = mano.quitarFicha(fichaId);
                FichaPlaced nueva = new FichaPlaced(base, jugador.getId(), turnoActual);
                tablero.agregarFichaAGrupo(grupoId, nueva);
                fichasInsertadas.add(nueva);
                continue;
            }

            FichaPlaced robo = tablero.buscarFichaPlacedGlobal(fichaId);
            if (robo != null) {
                tablero.removerFichaGlobal(fichaId);
                tablero.agregarFichaAGrupo(grupoId, robo);
                fichasInsertadas.add(robo);
                continue;
            }

            rollback(grupoId, estadoOriginalGrupo, manoAntes, fichasInsertadas, jugador);
            producer.mostrarError("Ficha no disponible: " + fichaId);
            producer.actualizarTablero(TableroMapper.toDTO(tablero));
            producer.actualizarManoJugador(FichaMapper.toDTO(jugador.getMano().getFichas()));
            return;
        }
        boolean pareceSet = esPotencialmenteSet(fichasInsertadas);
        Grupo grupoCorrecto;
        if (pareceSet) {
            grupoCorrecto = new GrupoNumero(grupoId, fichasInsertadas);
        } else {
            grupoCorrecto = new GrupoSecuencia(grupoId, fichasInsertadas);
        }
        tablero.agregarGrupo(grupoCorrecto);
        if (tablero.grupoEstaVacio(grupoId)) {
            tablero.removerGrupo(grupoId);
        }

        producer.actualizarTablero(TableroMapper.toDTO(tablero));
        producer.actualizarManoJugador(FichaMapper.toDTO(jugador.getMano().getFichas()));
    }

    private boolean esPotencialmenteSet(List<FichaPlaced> fichas) {
        List<Ficha> normales = fichas.stream()
                .map(FichaPlaced::getFicha)
                .filter(f -> !f.isEsComodin())
                .collect(Collectors.toList());

        if (normales.isEmpty() || normales.size() < 2) {
            return true;
        }
        int numRef = normales.get(0).getNumero();
        for (Ficha f : normales) {
            if (f.getNumero() != numRef) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void tomarFicha() {
        Jugador jugador = turno.getJugadorActual();
        String jugadorId = jugador.getId();

        try {
            if (!turno.esTurnoDelJugador(jugadorId)) {
                producer.mostrarError("No es tu turno de juego.");
                return;
            }
        } catch (Exception e) {
            LOG.severe(e.getMessage());
            producer.mostrarError("Error interno al procesar el grupo.");
        }

        if (jugador == null) {
            producer.mostrarError("Jugador no encontrado.");
            return;
        }

        Ficha ficha = sopa.tomarFicha();
        jugador.getMano().agregarFicha(ficha);
        List<Ficha> fichasJugador = jugador.getMano().getFichas();
        producer.actualizarManoJugador(FichaMapper.toDTO(fichasJugador));

        TableroDTO tableroActualizado = FichaMapper.toDTO(tablero);
        producer.actualizarTablero(tableroActualizado);

        producer.actualizarTurno(jugadorId);
    }

    @Override
    public void agregarJugador(Jugador jugador) {
        jugadores.put(jugador.getId(), jugador);
    }

    private void rollback(
            String grupoId,
            List<FichaPlaced> estadoOriginalGrupo,
            List<Ficha> estadoOriginalMano,
            List<FichaPlaced> fichasIntentadas,
            Jugador jugador
    ) {
        tablero.revivirGrupo(grupoId, estadoOriginalGrupo);
        jugador.getMano().restaurar(estadoOriginalMano);

        // 3. Restaurar fichas que robamos de OTROS grupos
        for (FichaPlaced fp : fichasIntentadas) {
            boolean estabaEnTarget = estadoOriginalGrupo.stream()
                    .anyMatch(old -> old.getFicha().getId().equals(fp.getFicha().getId()));

            boolean estabaEnMano = estadoOriginalMano.stream()
                    .anyMatch(old -> old.getId().equals(fp.getFicha().getId()));

            if (!estabaEnTarget && !estabaEnMano) {
                tablero.restaurarFicha(fp);
            }
        }
    }

    @Override
    public void terminarTurno() {
        Jugador jugador = turno.getJugadorActual();

        if (jugador == null) {
            producer.mostrarError("Jugador actual no definido.");
            return;
        }

        String jugadorIdInterno = jugador.getId();

        try {
            if (!turno.esTurnoDelJugador(jugadorIdInterno)) {
                producer.mostrarError("No puedes terminar el turno si no es el tuyo.");
                return;
            }

            Jugador jugadorActual = jugadores.get(jugadorIdInterno);
            if (jugadorActual == null) {
                producer.mostrarError("Jugador no encontrado.");
                return;
            }

            for (Grupo grupo : tablero.getGruposEnMesa()) {
                if (!grupo.validarReglas()) {
                    producer.mostrarError(
                            "No puedes terminar el turno: hay un grupo inválido en la mesa (requiere 3+ fichas válidas)."
                    );
                    return;
                }
            }

            tablero.marcarFichasConfirmadas(jugadorIdInterno);

            if (jugadorActual.getMano().getFichas().isEmpty()) {
                String ganadorIdPublico = jugador.getNombre();
                producer.juegoTerminado(ganadorIdPublico);
                return;
            }

            Jugador siguienteJugador = turno.avanzarTurno();
            if (siguienteJugador == null) {
                producer.mostrarError("No se pudo determinar el siguiente jugador.");
                return;
            }

            String siguienteJugadorIdPublico = siguienteJugador.getNombre();

            TableroDTO tableroActualizado = FichaMapper.toDTO(tablero);
            producer.actualizarTablero(tableroActualizado);
            producer.actualizarTurno(siguienteJugadorIdPublico);

        } catch (Exception e) {
            LOG.severe("Error al terminar el turno: " + e.getMessage());
            producer.mostrarError("Ocurrió un error inesperado al finalizar el turno.");
        }
    }

}
