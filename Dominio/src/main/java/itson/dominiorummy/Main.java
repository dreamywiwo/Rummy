package itson.dominiorummy;

import java.util.List;
import java.util.logging.Logger;
import itson.producerdominio.facade.IProducerDominio;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.TableroDTO; 
import itson.dominiorummy.interfaces.IDominio;
import itson.dominiorummy.entidades.Tablero;
import itson.dominiorummy.entidades.Turno;
import itson.dominiorummy.entidades.Grupo;
import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.Jugador;
import itson.dominiorummy.mappers.FichaMapper;
import itson.dominiorummy.util.ValidadorGrupo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dana Chavez
 */

public class Dominio implements IDominio {
    
    private final Tablero tablero;
    private final IProducerDominio producer;
    private final Turno turno;
    private final Map<String, Jugador> jugadores;
    private static final Logger LOG = Logger.getLogger(Dominio.class.getName());

    public Dominio(Tablero tablero, IProducerDominio producer, Turno turno) {
        this.tablero = tablero;
        this.producer = producer;
        this.turno = turno;
        this.jugadores = new HashMap<>(); 
        
        this.jugadores.put("J1", new Jugador("J1", "Jugador1"));
        this.jugadores.get("J1").getMano().agregarFicha(new Ficha("1", 1, "Rojo"));
        this.jugadores.get("J1").getMano().agregarFicha(new Ficha("2", 1, "Rojo"));
        this.jugadores.get("J1").getMano().agregarFicha(new Ficha("3", 1, "Rojo"));
        this.turno.setJugadorTurnoId("J1");
    }

    public void crearGrupo(String jugadorId, List<FichaDTO> fichasDTO) {
        try {
            if (!turno.esTurnoDelJugador(jugadorId)) {
                producer.mostrarError("No es tu turno de juego.");
                return;
            }

            Jugador jugador = jugadores.get(jugadorId);
            if (jugador == null) {
                producer.mostrarError("Jugador no encontrado.");
                return;
            }

            List<Ficha> fichasEntidad = FichaMapper.toEntidad(fichasDTO);

            if (!jugador.getMano().contieneFichas(fichasEntidad)) {
                producer.mostrarError("No posees todas las fichas seleccionadas en tu mano.");
                return;
            }
            
            if (!ValidadorGrupo.esValido(fichasEntidad)) {
                producer.mostrarError("El grupo debe ser de 3 o más fichas válidas.");
                return;
            }
            
            jugador.getMano().removerFichas(fichasEntidad);
            jugador.setPrimerGrupoBajado(true);
            
            Grupo nuevoGrupo = new Grupo(fichasEntidad, jugadorId);
            tablero.agregarGrupo(nuevoGrupo); 

            TableroDTO tableroActualizado = FichaMapper.toDTO(tablero);
            producer.actualizarTablero(tableroActualizado); 

            List<FichaDTO> manoActualizada = FichaMapper.toDTO(jugador.getMano().getFichas());
            producer.actualizarManoJugador(manoActualizada);

        } catch (Exception e) {
            LOG.severe(e.getMessage());
            producer.mostrarError("Error interno al procesar el grupo.");
        }
    }
}