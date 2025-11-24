/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.modelo;

import itson.producerjugador.facade.IProducerJugador;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.GrupoDTO;
import itson.rummydtos.JugadorDTO;
import itson.rummydtos.TableroDTO;
import itson.rummyeventos.actualizaciones.TurnoTerminadoEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana ChavezW
 */
public class Modelo implements IModelo, ISubject, IListener {

    private IProducerJugador producer;
    // Lista de observadores
    private List<IObserver> observers;

    // Estado interno del modelo
    private List<GrupoDTO> gruposEnTablero = new ArrayList<>();
    private JugadorDTO jugadorActual;
    private List<FichaDTO> fichasMano = new ArrayList<>();
    private List<JugadorDTO> otrosJugadores;
    private int fichasEnPozo;
    private String turnoActual;
    private boolean partidaTerminada;
    private String ganador;
    private String ultimaAccion;
    private boolean accionValida;
    private String mensajeError;

    //tendra al producer que se llamara para crear cada evento
    public Modelo(IProducerJugador producer) {
        this.producer = producer;
        this.observers = new ArrayList<>();
    }

    // Metodos que llama el control
    public void crearGrupo(List<FichaDTO> fichas) {
        producer.crearGrupo(fichas);
    }

    public void actualizarGrupo(String idGrupo, List<FichaDTO> fichas) {
    }

    public void terminarTurno() {
    }

    public void tomarFicha() {
    }

    // ACTUALIZACION DESDE TRADUCER
    /**
     * Configurar listener para recibir actualizaciones del Traducer
     */
    private void configurarTraducerListener() {
        // Cuando el Traducer reciba cambios del dominio, llamará a este método

        // Ejemplo (dependiendo de la implementacion del Traducer):
        // traducer.onEstadoActualizado((estadoDominio) -> {
        //     actualizarDesdeTraducer(estadoDominio);
        // });
    }

    /**
     * Actualizar el modelo desde el estado del dominio (Traducer) Este método
     * se llama cuando el Traducer recibe actualizaciones
     */
    public void actualizarDesdeTraducer() {

        // Podemos manejar separada la actualizacion get
        // para cada tipo de evento o en si actualizar todo
        // Notificar a la vista (UI_TurnoJugador)
        notificarObservers();
    }

    // Implementacion de IModelo
    @Override
    public List<GrupoDTO> getGruposEnTablero() {
        return new ArrayList<>(gruposEnTablero);
    }

    @Override
    public JugadorDTO getJugadorActual() {
        return jugadorActual;
    }

    @Override
    public List<FichaDTO> getFichasMano() {
        return jugadorActual != null ? jugadorActual.getFichasMano() : new ArrayList<>();
    }

    @Override
    public List<JugadorDTO> getOtrosJugadores() {
        return new ArrayList<>(otrosJugadores);
    }

    @Override
    public int getFichasEnPozo() {
        return fichasEnPozo;
    }

    @Override
    public String getTurnoActual() {
        return turnoActual;
    }

    @Override
    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    @Override
    public String getGanador() {
        return ganador;
    }

    @Override
    public String getUltimaAccion() {
        return ultimaAccion;
    }

    @Override
    public boolean isAccionValida() {
        return accionValida;
    }

    @Override
    public String getMensajeError() {
        return mensajeError;
    }

    // OBSERVER
    @Override
    public void suscribir(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void notificar(IObserver observer) {
        observer.update(this);
    }

    @Override
    public void notificarObservers() {
        for (IObserver observer : observers) {
            observer.update(this);
        }
    }

    @Override
    public void terminoTurno(TurnoTerminadoEvent event) {
        this.turnoActual = event.getNuevoTurnoJugador();
        notificarObservers();
    }

    @Override
    public void recibirTablero(TableroDTO tableroDTO) {
        if (tableroDTO != null && tableroDTO.getGrupos() != null) {
            this.gruposEnTablero = tableroDTO.getGrupos();
            notificarObservers();
        }
    }

    @Override
    public void recibirMano(List<FichaDTO> mano) {
        if (mano != null) {
            this.fichasMano = mano;
            if (this.jugadorActual != null) {
                jugadorActual.setFichasMano(fichasMano);
            }
        }
        notificarObservers();
    }

    @Override
    public void recibirSopa(int cantidad) {
        this.fichasEnPozo = cantidad;
        notificarObservers();
    }

    @Override
    public void recibirError(String mensaje) {
        this.mensajeError = mensaje;
        notificarObservers();
    }

}
