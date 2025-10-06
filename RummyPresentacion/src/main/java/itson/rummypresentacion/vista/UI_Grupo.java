/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.modelo.IModelo;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UI_Grupo extends ComponenteBase {

    private List<UI_Ficha> fichas;
    private UI_Tablero padre; // Ahora el padre es UI_Tablero
    private List<UI_Ficha> fichasSeleccionadas;
    private boolean esMano;
    private Set<String> idsFichasSeleccionadas = new HashSet<>();
    private List<FichaDTO> manoAnterior = new ArrayList<>();
    private boolean esGrupoDeTablero = false;

    public UI_Grupo(String id) {
        super(id);
        this.fichas = new ArrayList<>();
        this.fichasSeleccionadas = new ArrayList<>();
        this.esMano = "mano_jugador".equals(id);
        inicializarComponente();
    }

    private void inicializarComponente() {
        if (esMano) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
            setBackground(new Color(39, 41, 72));
        } else {
            setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            setBackground(new Color(200, 200, 200, 100));
            setOpaque(false);
        }
        setOpaque(true);
    }

    @Override
    public void actualizar(IModelo modelo) {
        try {
            if (esGrupoDeTablero) {
// Evita limpiar las fichas de los grupos del tablero
                return;
            }
            List<FichaDTO> manoActual = modelo.getManoJugadorActual();
            if (manoActual != null && manoActual.equals(manoAnterior)) {
                System.out.println("DEBUG - No hay cambios en la mano,omitiendo actualización");
                return;
            }
            System.out.println("UI_Grupo '" + getId() + "' actualizandodesde modelo...");
            preservarSelecciones();
            limpiarGrupo();
            if (esMano) {
                actualizarManoDesdeModelo(modelo);
                restaurarSelecciones();
                manoAnterior = new ArrayList<>(manoActual);
            }
            System.out.println("UI_Grupo '" + getId() + "' actualizadocon " + fichas.size() + " fichas, "
                    + fichasSeleccionadas.size() + " seleccionadas");
        } catch (Exception ex) {
            System.err.println("Error actualizando UI_Grupo: "
                    + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Preserva los IDs de las fichas seleccionadas antes de limpiar
     */
    private void preservarSelecciones() {
        idsFichasSeleccionadas.clear();
        for (UI_Ficha ficha : fichasSeleccionadas) {
            idsFichasSeleccionadas.add(ficha.getId());
        }
        System.out.println("DEBUG - Selecciones preservadas: " + idsFichasSeleccionadas.size() + " fichas");
    }

    /**
     * Restaura las selecciones después de recrear las fichas
     */
    private void restaurarSelecciones() {
        fichasSeleccionadas.clear();
        for (UI_Ficha ficha : fichas) {
            if (idsFichasSeleccionadas.contains(ficha.getId())) {
                ficha.setSelected(true);
                fichasSeleccionadas.add(ficha);
                System.out.println("DEBUG - Ficha restaurada como seleccionada: " + ficha.getId());
            }
        }
        System.out.println("DEBUG - Selecciones restauradas: " + fichasSeleccionadas.size() + " fichas");
    }

    private void actualizarManoDesdeModelo(IModelo modelo) {
        try {
            List<FichaDTO> fichasMano = modelo.getManoJugadorActual();
            System.out.println("DEBUG - UI_Grupo.actualizarManoDesdeModelo:");
            System.out.println("  Fichas del modelo: " + (fichasMano != null ? fichasMano.size() : "null"));

            if (fichasMano != null && !fichasMano.isEmpty()) {
                for (FichaDTO fichaDTO : fichasMano) {
                    UI_Ficha fichaUI = new UI_Ficha(fichaDTO);
                    agregarFicha(fichaUI);
                }
                System.out.println("Mano actualizada con " + fichasMano.size() + " fichas");
            } else {
                System.out.println("DEBUG - La mano está vacía o es null");
            }
        } catch (Exception e) {
            System.err.println("Error en actualizarManoDesdeModelo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void agregarFicha(UI_Ficha ficha) {
        configurarFichaParaSeleccion(ficha);
        fichas.add(ficha);
        agregarComponente(ficha);
        add(ficha);
        revalidate();
        repaint();

        System.out.println("DEBUG - Ficha agregada: " + ficha.getId());
    }

    private void configurarFichaParaSeleccion(UI_Ficha ficha) {
        ficha.addActionListener(e -> {
            boolean estabaSeleccionada = ficha.isSelected();

            if (estabaSeleccionada) {
                fichasSeleccionadas.add(ficha);
                idsFichasSeleccionadas.add(ficha.getId()); // Actualizar el conjunto
                System.out.println("Ficha seleccionada: " + ficha.getId());
            } else {
                fichasSeleccionadas.remove(ficha);
                idsFichasSeleccionadas.remove(ficha.getId()); // Actualizar el conjunto
                System.out.println("Ficha deseleccionada: " + ficha.getId());
            }

            // Notificar al padre sobre la selección cambiada
            if (padre != null) {
                List<String> fichasIds = obtenerIdsFichasSeleccionadas();
                padre.onFichasSeleccionadas(fichasIds);
            } else {
                System.err.println("ERROR: UI_Grupo no tiene referencia al padre (UI_Tablero)");
            }
        });
    }

    /**
     * Obtiene los IDs de las fichas seleccionadas
     */
    public List<String> obtenerIdsFichasSeleccionadas() {
        List<String> ids = new ArrayList<>();
        for (UI_Ficha ficha : fichasSeleccionadas) {
            ids.add(ficha.getId());
        }
        return ids;
    }

    /**
     * Obtiene las FichaDTO de las fichas seleccionadas
     */
    public List<FichaDTO> getFichasSeleccionadasDTO() {
        List<FichaDTO> fichasDTO = new ArrayList<>();
        for (UI_Ficha ficha : fichasSeleccionadas) {
            fichasDTO.add(ficha.getFichaDTO());
        }
        return fichasDTO;
    }

    public List<UI_Ficha> getFichasSeleccionadas() {
        return new ArrayList<>(fichasSeleccionadas);
    }

    public void limpiarSeleccion() {
        for (UI_Ficha ficha : fichasSeleccionadas) {
            ficha.setSelected(false);
        }
        fichasSeleccionadas.clear();
    }

    public void removerFichasSeleccionadas() {
        for (UI_Ficha ficha : fichasSeleccionadas) {
            fichas.remove(ficha);
            removerComponente(ficha);
            remove(ficha);
        }
        fichasSeleccionadas.clear();
        revalidate();
        repaint();
    }

    public void limpiarGrupo() {
        for (UI_Ficha ficha : fichas) {
            removerComponente(ficha);
            remove(ficha);
        }
        fichas.clear();
        fichasSeleccionadas.clear();
        revalidate();
        repaint();
    }

    public List<UI_Ficha> getFichas() {
        return new ArrayList<>(fichas);
    }

    /**
     * Establece el padre (UI_Tablero) para este grupo
     */
    public void setPadre(UI_Tablero padre) {
        this.padre = padre;
        System.out.println("UI_Grupo '" + getId() + "' - Padre establecido: " + (padre != null ? padre.getId() : "null"));
    }

    /**
     * Método de compatibilidad - establece el padre que es UI_Tablero Este
     * método es necesario para mantener la compatibilidad con el código
     * existente
     */
    public void setTablero(UI_Tablero tablero) {
        setPadre(tablero); // Llama a setPadre para mantener la consistencia
        System.out.println("UI_Grupo '" + getId() + "' - Tablero establecido como padre");
    }

    public void setEsGrupoDeTablero(boolean esTablero) {
        this.esGrupoDeTablero = esTablero;
    }

}
