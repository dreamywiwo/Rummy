/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import itson.rummypresentacion.modelo.IModelo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UI_Tablero extends ComponenteBase {
    private UI_Grupo grupoMano; 
    private UI_TurnoJugador padre; // Referencia al root
    private Point ultimaCasillaSeleccionada;
    private List<UI_Grupo> gruposTablero; // Para grupos en el tablero
    
    public UI_Tablero(String id) {
        super(id);
        this.ultimaCasillaSeleccionada = null;
        this.gruposTablero = new ArrayList<>();
        inicializarComponente();
    }

    private void inicializarComponente() {
        setPreferredSize(new Dimension(1230, 660));
        setBackground(new Color(230, 230, 230));
        setLayout(null); // Usamos layout absoluto para posicionar grupos
        configurarSeleccionCasillas();
    }

    @Override
    public void actualizar(IModelo modelo) {
        try {
            System.out.println("Actualizando UI_Tablero desde modelo...");
            
            // 1. Actualizar grupos del tablero desde el modelo
            actualizarGruposTablero(modelo);
            
            // 2. Propagar actualización a componentes hijos (incluyendo grupoMano)
            super.actualizar(modelo);
            
            // 3. Repintar
            this.revalidate();
            this.repaint();
            
        } catch (Exception ex) {
            System.err.println("Error actualizando tablero: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Actualiza los grupos del tablero desde el modelo
     */
    private void actualizarGruposTablero(IModelo modelo) {
        // Limpiar grupos existentes del tablero (no la mano)
        for (UI_Grupo grupo : gruposTablero) {
            removerComponente(grupo);
            remove(grupo);
        }
        gruposTablero.clear();
        
        // Obtener grupos del modelo y crear UI_Grupo para cada uno
        List<GrupoDTO> gruposDTO = modelo.getGruposTablero();
        if (gruposDTO != null) {
            System.out.println("Pintando " + gruposDTO.size() + " grupos en el tablero");
            
            int xPos = 10;
            int yPos = 10;
            
            for (int i = 0; i < gruposDTO.size(); i++) {
                GrupoDTO grupoDTO = gruposDTO.get(i);
                UI_Grupo grupoUI = new UI_Grupo("grupo_tablero_" + i);
                grupoUI.setPadre(this); // El tablero es el padre de los grupos del tablero
                
                // Posicionar el grupo en el tablero
                grupoUI.setBounds(xPos, yPos, 400, 100);
                grupoUI.setBackground(new Color(200, 200, 200, 100));
                
                // Agregar fichas al grupo
                for (FichaDTO fichaDTO : grupoDTO.getFichas()) {
                    UI_Ficha fichaUI = new UI_Ficha(fichaDTO);
                    grupoUI.agregarFicha(fichaUI);
                }
                
                // Agregar al composite y al panel
                agregarComponente(grupoUI);
                gruposTablero.add(grupoUI);
                add(grupoUI);
                
                // Actualizar posición para el siguiente grupo
                xPos += 420;
                if (xPos > 800) {
                    xPos = 10;
                    yPos += 120;
                }
            }
        }
    }

    /**
     * Método para que los grupos hijos notifiquen eventos
     */
    public void onFichasSeleccionadas(List<String> fichasIds) {
        System.out.println("UI_Tablero: Propagando selección de " + fichasIds.size() + " fichas al padre");     
        // Propagar la notificación al padre (UI_TurnoJugador)
        if (padre != null) {
            padre.onFichasSeleccionadas(fichasIds);
        } else {
            System.err.println("ERROR: UI_Tablero no tiene referencia al padre");
        }
    }

    /**
     * Método para que los grupos notifiquen creación de grupos
     */
    public void onGrupoCreado(List<FichaDTO> fichasDTO) {
        if (padre != null) {
            // Notificar al controlador para crear el grupo
            // Esto podría necesitar ajustes dependiendo de tu lógica
        }
    }

    public void setGrupoMano(UI_Grupo grupoMano) {
        this.grupoMano = grupoMano;
        // El grupoMano es un hijo especial - se maneja en el contenedor de mano, no en el tablero visual
        System.out.println("GrupoMano conectado al tablero");
    }

    public void setPadre(UI_TurnoJugador padre) {
        this.padre = padre;
    }

    // Resto del código permanece igual...
    private void configurarSeleccionCasillas() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isEnabled()) {
                    System.out.println("Tablero deshabilitado - ignorando clic");
                    return;
                }
                
                Point punto = e.getPoint();
                Point casilla = encontrarCasilla(punto);
                
                if (casilla != null) {
                    System.out.println("Casilla seleccionada: [" + casilla.x + "," + casilla.y + "]");
                    ultimaCasillaSeleccionada = casilla;

                    // Notificar al padre (UI_TurnoJugador) sobre el clic en el tablero
                    if (padre != null && grupoMano != null) {
                        List<String> fichasSeleccionadasIds = grupoMano.obtenerIdsFichasSeleccionadas();
                        padre.onCeldaTableroClickeada(casilla, fichasSeleccionadasIds);
                    } else {
                        System.out.println("ERROR: Padre o grupoMano no configurados");
                    }
                    
                    repaint(); 
                }
            }
        });
    }

    private Point encontrarCasilla(Point punto) {
        int cellWidth = getWidth() / 10;
        int cellHeight = getHeight() / 4;
        
        int columna = Math.min(9, Math.max(0, punto.x / cellWidth));
        int fila = Math.min(3, Math.max(0, punto.y / cellHeight));
        
        return new Point(fila, columna);
    }
    
    /**
     * Limpia el tablero - SOLO LA VISTA, no el estado del modelo
     */
    public void limpiarTablero() {
        // Limpiar del composite
        for (IComponente componente : getComponentes()) {
            if (componente instanceof UI_Ficha) {
                removerComponente(componente);
            }
        }
        
        // Limpiar del panel Swing
        removeAll();
        
        ultimaCasillaSeleccionada = null;
        
        revalidate();
        repaint();
        System.out.println("Vista del tablero limpiada");
    }

    /**
     * Muestra información de debug del tablero
     */
    public void mostrarEstadoTablero() {
        System.out.println("UI_Tablero - Estado de la vista:");
        System.out.println("Componentes hijos: " + getComponentes().size());
        System.out.println("Última casilla seleccionada: " + ultimaCasillaSeleccionada);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int cellWidth = Math.max(getWidth() / 10, 40);
        int cellHeight = Math.max(getHeight() / 4, 40);
        
        // Dibujar grid del tablero
        g2.setColor(Color.LIGHT_GRAY);
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                int x = columna * cellWidth;
                int y = fila * cellHeight;
                g2.drawRect(x, y, cellWidth, cellHeight);

                // Resaltar casilla seleccionada
                if (ultimaCasillaSeleccionada != null && 
                    ultimaCasillaSeleccionada.x == fila && ultimaCasillaSeleccionada.y == columna) {
                    g2.setColor(new Color(255, 255, 0, 100));
                    g2.fillRect(x + 1, y + 1, cellWidth - 2, cellHeight - 2);
                    g2.setColor(Color.LIGHT_GRAY);
                }
            }
        }

        // Dibujar coordenadas
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna < 10; columna++) {
                int x = columna * cellWidth + 5;
                int y = fila * cellHeight + 15;
                g2.drawString(fila + "," + columna, x, y);
            }
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1230, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
