/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.facade;

import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.FichaPlaced;
import itson.dominiorummy.entidades.Grupo;
import itson.dominiorummy.entidades.Jugador;
import itson.dominiorummy.entidades.Mano;
import itson.dominiorummy.entidades.Tablero;
import itson.dominiorummy.entidades.Turno;
import itson.producerdominio.facade.IProducerDominio;
import itson.rummydtos.FichaDTO;
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
    private final Map<String, Jugador> jugadores;
    private static final Logger LOG = Logger.getLogger(itson.dominiorummy.facade.Dominio.class.getName());

    public Dominio(Tablero tablero, IProducerDominio producer, Turno turno) {
        this.tablero = tablero;
        this.producer = producer;
        this.turno = turno;
        this.jugadores = new HashMap<>();

    }

    public void crearGrupo(String jugadorId, List<FichaDTO> fichasDTO) {

        Jugador jugador = turno.getJugadorActual();
        Mano mano = jugador.getMano();
        int turnoActual = turno.getNumeroTurno();

        List<FichaPlaced> fichasAInsertar = new ArrayList<>();

        // 1. Procesar cada fichaDTO: mano → tablero → error
        for (FichaDTO dto : fichasDTO) {
            String fichaId = dto.getId();

            // A) Buscar en la mano
            if (mano.tieneFicha(fichaId)) {
                Ficha base = mano.quitarFicha(fichaId);
                FichaPlaced fp = new FichaPlaced(base, jugador.getId(), turnoActual);
                fichasAInsertar.add(fp);
                continue;
            }

            // B) Buscar en todos los grupos del tablero
            FichaPlaced fpDesdeTablero = tablero.buscarFichaPlacedGlobal(fichaId);

            if (fpDesdeTablero != null) {
                tablero.removerFichaGlobal(fichaId); // quitar de donde estaba
                fichasAInsertar.add(fpDesdeTablero);
                continue;
            }

            // C) No viene de ningún lado
            producer.mostrarError("La ficha " + fichaId + " no está disponible.");
            return;
        }

        // 2. Crear grupo usando la factory del tablero
        //    NOTA: este método determina automáticamente si es
        //    GrupoNumero o GrupoSecuencia
        Grupo grupoNuevo = tablero.crearGrupoDesdeFichasPlaced(fichasAInsertar);

        if (grupoNuevo == null) {
            // restaurar mano si ya se quitó fichas
            for (FichaPlaced fp : fichasAInsertar) {
                if (fp.getPlacedBy().equals(jugadorId)
                        && fp.getPlacedInTurn() == turnoActual) {

                    mano.agregarFicha(fp.getFicha());

                } else {
                    tablero.restaurarFicha(fp);
                }
            }

            producer.mostrarError("Las fichas no forman un grupo válido.");
            return;
        }

        // 3. Registrar el grupo en el tablero
        tablero.agregarGrupo(grupoNuevo);

        // 4. Actualizar UI
        // producer.actualizarTablero(TableroMapper.toDTO(tablero));
    }

    public void actualizarGrupo(String grupoId, List<FichaDTO> fichasNuevasDTO) {

        Jugador jugador = turno.getJugadorActual();
        Mano mano = jugador.getMano();
        int turnoActual = turno.getNumeroTurno();

        // Snapshot para rollback
        List<FichaPlaced> estadoOriginal = tablero.obtenerFichasDeGrupo(grupoId)
                .stream()
                .map(FichaPlaced::clonar)
                .collect(Collectors.toList());

        List<FichaPlaced> fichasAntes = tablero.obtenerFichasDeGrupo(grupoId);
        List<Ficha> manoAntes = mano.clonarContenido();

        // Límpiar el grupo actual
        tablero.limpiarGrupo(grupoId);

        List<FichaPlaced> fichasInsertadas = new ArrayList<>();

        // 1. Reconstrucción desde cero
        for (FichaDTO dto : fichasNuevasDTO) {

            String fichaId = dto.getId();

            // 1) Estaba antes en el mismo grupo
            FichaPlaced existenteAntes = fichasAntes.stream()
                    .filter(fp -> fp.getFicha().getId().equals(fichaId))
                    .findFirst()
                    .orElse(null);

            if (existenteAntes != null) {
                tablero.agregarFichaAGrupo(grupoId, existenteAntes);
                fichasInsertadas.add(existenteAntes);
                continue;
            }

            // 2) Viene de mano
            if (mano.tieneFicha(fichaId)) {
                Ficha base = mano.quitarFicha(fichaId);
                FichaPlaced nueva = new FichaPlaced(base, jugador.getId(), turnoActual);
                tablero.agregarFichaAGrupo(grupoId, nueva);
                fichasInsertadas.add(nueva);
                continue;
            }

            // 3) Viene de otro grupo del tablero
            FichaPlaced desdeOtroGrupo = tablero.buscarFichaPlacedGlobal(fichaId);
            if (desdeOtroGrupo != null) {
                tablero.removerFichaGlobal(fichaId);
                tablero.agregarFichaAGrupo(grupoId, desdeOtroGrupo);
                fichasInsertadas.add(desdeOtroGrupo);
                continue;
            }

            // 4) No viene de ningún lado
            rollback(grupoId, estadoOriginal, manoAntes, jugador);
            producer.mostrarError("Ficha no disponible: " + fichaId);
            return;
        }

        // 2. Validar reglas del grupo
        if (!tablero.validarReglasDeGrupo(grupoId)) {
            rollback(grupoId, estadoOriginal, manoAntes, jugador);
            producer.mostrarError("El grupo resultante no es válido");
            return;
        }

        // 3. Si quedó vacío, se elimina
        if (tablero.grupoEstaVacio(grupoId)) {
            tablero.removerGrupo(grupoId);
        }

        // 4. Actualizar UI
        //       producer.actualizarTablero(TableroMapper.toDTO(tablero));
    }

    public void agregarJugador(Jugador jugador) {
        jugadores.put(jugador.getId(), jugador);
    }

    private void rollback(
            String grupoId,
            List<FichaPlaced> estadoOriginalGrupo,
            List<Ficha> estadoOriginalMano,
            Jugador jugador
    ) {
        tablero.limpiarGrupo(grupoId);

        for (FichaPlaced fp : estadoOriginalGrupo) {
            tablero.agregarFichaAGrupo(grupoId, fp);
        }

        jugador.getMano().restaurar(estadoOriginalMano);
    }

}
