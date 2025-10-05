/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.modelo;

import entidades.EstadoJuego;
import entidades.Ficha;
import entidades.Grupo;
import entidades.Jugador;
import entidades.Movimiento;
import entidades.Tablero;
import entidades.Turno;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Dana Chavez
 */
public class Modelo implements IModelo, ISubject {

    @Override
    public void terminarTurno(String jugadorId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void pasarTurno() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void separarGrupo(String grupoId, String jugadorId) throws Exception {
        if (grupoId == null) {
            throw new Exception("Grupo no especificado");
        }
        Jugador jugador = getJugador(jugadorId);
        Grupo grupo = getGrupo(grupoId);
        if (!estado.getTablero().getGrupos().remove(grupo)) {
            throw new Exception("El grupo no estaba en el tablero");
        }
        jugador.getMano().addAll(grupo.getFichas());
        grupo.getFichas().clear();
    }

    @Override
    public void sustituirFicha(String jugadorId, String grupoId, Ficha fichaNueva) throws Exception {
        if (grupoId == null) {
            throw new Exception("Grupo no especificado");
        }
        if (fichaNueva == null) {
            throw new Exception("Ficha nueva requerida");
        }
        Jugador jugador = getJugador(jugadorId);
        Grupo grupo = getGrupo(grupoId);

        if (!jugador.getMano().contains(fichaNueva)) {
            throw new Exception("La ficha a sustituir no está en la mano del jugador");
        }
        Movimiento mov = new Movimiento();
        mov.setTipo("extender grupo");
        mov.setJugador(jugador);
        mov.setGrupo(grupo);
        mov.setFicha(fichaNueva);
        registrarMovimiento(estado, mov);
    }

    @Override
    public void cambiarFicha(String jugadorId, String grupoId) throws Exception {
        if (grupoId == null) {
            throw new Exception("Grupo no especificado");
        }
        Jugador jugador = getJugador(jugadorId);
        Grupo grupo = getGrupo(grupoId);

        if (jugador.getMano().isEmpty()) {
            throw new Exception("El jugador no tiene fichas para cambiar");
        }
        if (grupo.getFichas().isEmpty()) {
            throw new Exception("El grupo no tiene fichas para cambiar");
        }
        Ficha fichaQueSale = grupo.getFichas().get(grupo.getFichas().size() - 1);
        for (int i = 0; i < jugador.getMano().size(); i++) {
            Ficha candidata = jugador.getMano().get(i);
            grupo.getFichas().remove(grupo.getFichas().size() - 1);
            grupo.getFichas().add(candidata);
            boolean valido = validarGrupoColor(grupo) || validarGrupoConsecutivo(grupo);
            if (valido) {
                jugador.getMano().remove(i);
                jugador.getMano().add(fichaQueSale);
                return;
            } else {
                grupo.getFichas().remove(grupo.getFichas().size() - 1);
                grupo.getFichas().add(fichaQueSale);
            }
        }

        throw new Exception("No hay ficha en la mano que permita un cambio válido");
    }

    @Override
    public Ficha tomarFicha(String jugadorId) throws Exception {
        Jugador jugador = getJugador(jugadorId);
        List<Ficha> pozo = estado.getPozoDeFichas(); 

        if (pozo == null || pozo.isEmpty()) {
            throw new Exception("No hay más fichas para tomar.");
        }
        
        Ficha fichaTomada = pozo.remove(0);
        jugador.getMano().add(fichaTomada);
        notificarObservers();
        return fichaTomada;
    }

    @Override
    public void terminarTurno(String jugadorId) throws Exception {
        System.out.println("Turno terminado para el jugador: " + jugadorId);
    }

    @Override
    public void pasarTurno() throws Exception {
        List<Jugador> jugadores = estado.getJugadores();
        if (jugadores == null || jugadores.size() < 2) {
            return; 
        }
        Jugador jugadorActual = estado.getTurnoActual().getJugadorActual();

        int indiceActual = jugadores.indexOf(jugadorActual);

        int indiceSiguiente = (indiceActual + 1) % jugadores.size();
        Jugador siguienteJugador = jugadores.get(indiceSiguiente);

        Turno nuevoTurno = new Turno(estado.getTurnoActual().getNumero() + 1, siguienteJugador);
        
        estado.setTurnoActual(nuevoTurno);

        notificarObservers();
    }
    
    @Override
    public void suscribir(IObserver observer) {
        observers.add(observer);
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

    private List<IObserver> observers = new ArrayList<>();
    EstadoJuego estado = new EstadoJuego();
    Tablero tablero = new Tablero();

    public void iniciarNuevaPartida(List<Jugador> jugadores) throws Exception {
        estado.getTablero().getGrupos().clear();
        estado.setJugadores(jugadores);
        Turno primerTurno = new Turno(1, jugadores.get(0));
        estado.setTurnoActual(primerTurno);
        if (jugadores.isEmpty()) {
            throw new Exception("No se pudo iniciar la partida");
        }
    }

    public void registrarMovimiento(EstadoJuego estado, Movimiento movimiento) throws Exception {
        if (movimiento == null) {
            throw new Exception("Movimiento nulo o invalido");
        }
        if (estado == null) {
            throw new Exception("El estado no puede ser nulo");
        }
        if (movimiento.getJugador() == null) {
            throw new Exception("El movimiento no tiene un jugador asignado");
        }
        if (movimiento.getTipo() == null) {
            throw new Exception("Realice un movimiento valido");
        }

        switch (movimiento.getTipo().toLowerCase()) {
            case "bajar grupo": {
                Grupo grupo = movimiento.getGrupo();
                if (grupo.getFichas().size() < 3) {
                    throw new Exception("Un grupo debe de tener al menos 3 fichas");
                }
                boolean color = validarGrupoColor(grupo);
                boolean serie = validarGrupoConsecutivo(grupo);
                if (color || serie) {
                    grupo.setEsValido(true);
                    estado.getTablero().getGrupos().add(movimiento.getGrupo());
                    for (Ficha f : grupo.getFichas()) {
                        movimiento.getJugador().getMano().remove(f);
                    }
                } else {
                    grupo.setEsValido(false);
                    throw new Exception("Movimiento no valido");
                }
                break;
            }
            case "extender grupo": {
                Grupo grupo = movimiento.getGrupo();
                Ficha ficha = movimiento.getFicha();
                if (!estado.getTablero().getGrupos().contains(grupo)) {
                    throw new Exception("El grupo a extender no existe en el tablero");
                }
                if (!movimiento.getJugador().getMano().contains(ficha)) {
                    throw new Exception("La ficha indicada no está en la mano del jugador");
                }
                grupo.getFichas().add(ficha);
                boolean color = validarGrupoColor(grupo);
                boolean serie = validarGrupoConsecutivo(grupo);
                if (serie || color) {
                    grupo.setEsValido(true);
                    movimiento.getJugador().getMano().remove(ficha);
                } else {
                    grupo.getFichas().remove(grupo.getFichas().size() - 1);
                    grupo.setEsValido(false);
                    throw new Exception("Movimiento no valido");
                }
                break;
            }
            default:
                throw new Exception("Movimiento no reconocido" + movimiento.getTipo());
        }

    }

    public boolean validarGrupoConsecutivo(Grupo grupo) throws Exception {
        List<Ficha> fichas = grupo.getFichas();
        String color = fichas.get(0).getColor();
        boolean mismoColor = fichas.stream().allMatch(f -> f.getColor().equals(color));
        if (!mismoColor) {
            return false;
        }
        try {
            List<Integer> valores = fichas.stream().map(f
                    -> Integer.parseInt(f.getValor()))
                    .sorted().toList();
            for (int i = 1; i < valores.size(); i++) {
                if (valores.get(i) != valores.get(i - 1) + 1) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validarGrupoColor(Grupo grupo) {
        List<Ficha> fichas = grupo.getFichas();
        if (fichas == null || fichas.size() < 3) {
            return false;
        }
        String valor = fichas.get(0).getValor();
        for (Ficha f : fichas) {
            if (!f.getValor().equals(valor)) {
                return false;
            }
        }
        for (int i = 0; i < fichas.size(); i++) {
            for (int j = i + 1; j < fichas.size(); j++) {
                if (fichas.get(i).getColor().equals(fichas.get(j).getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void crearGrupo(List<Ficha> fichas, String jugadorId) throws Exception {
        if (fichas == null || fichas.size() < 3) {
            throw new Exception("Un grupo debe tener al menos 3 fichas");
        }
        Jugador jugador = getJugador(jugadorId);
        Grupo grupo = new Grupo();
        grupo.setId(UUID.randomUUID().toString());
        grupo.setFichas(new ArrayList<>(fichas));
        Movimiento mov = new Movimiento();
        mov.setTipo("bajar grupo");
        mov.setJugador(jugador);
        mov.setGrupo(grupo);

        registrarMovimiento(estado, mov);
    }

    private Jugador getJugador(String jugadorId) throws Exception {
        if (jugadorId == null || jugadorId.isBlank()) {
            if (estado.getTurnoActual() != null && estado.getTurnoActual().getJugadorActual() != null) {
                return estado.getTurnoActual().getJugadorActual();
            }
            throw new Exception("No hay jugador actual y no se proporcionó jugadorId");
        }
        List<Jugador> jugadores = estado.getJugadores();
        if (jugadores == null || jugadores.isEmpty()) {
            throw new Exception("La lista de jugadores no está inicializada o está vacía");
        }
        for (Jugador j : jugadores) {
            if (Objects.equals(j.getId(), jugadorId) || Objects.equals(j.getNombre(), jugadorId)) {
                return j;
            }
        }
        throw new Exception("Jugador no encontrado: " + jugadorId);
    }

    private Grupo getGrupo(String grupoId) throws Exception {
        if (grupoId == null || grupoId.isBlank()) {
            throw new Exception("Grupo no especificado (grupoId vacío)");
        }

        if (estado.getTablero() == null) {
            estado.setTablero(new Tablero());
        }

        List<Grupo> grupos = estado.getTablero().getGrupos();
        if (grupos == null) {
            grupos = new ArrayList<>();
            estado.getTablero().setGrupos(grupos);
        }
        for (Grupo g : grupos) {
            if (Objects.equals(g.getId(), grupoId)) {
                return g;
            }
        }
        throw new Exception("Grupo no encontrado: " + grupoId);
    }
}
