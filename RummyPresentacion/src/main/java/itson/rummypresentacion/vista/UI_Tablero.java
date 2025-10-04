/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dana Chavez
 */
public class UI_Tablero extends javax.swing.JPanel {
    private Map<String, UI_Ficha> fichasEnTablero;
    private int[][] celdasOcupadas;
    private UI_Mano mano;
    private Point ultimaCasillaSeleccionada;
    
    public UI_Tablero() {
        this.fichasEnTablero = new HashMap<>();
        this.celdasOcupadas = new int[4][10];
        this.ultimaCasillaSeleccionada = null;
        initComponents();
        setPreferredSize(new Dimension(1230, 660));
        setBackground(new Color(230, 230, 230));
        setLayout(null);
        
        configurarSeleccionCasillas();
    }
    
    // 🔥 CORRECCIÓN: Setter para la mano
    public void setMano(UI_Mano mano) {
        this.mano = mano;
        System.out.println("✅ Mano conectada al tablero");
    }
    
    private void configurarSeleccionCasillas() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point punto = e.getPoint();
                Point casilla = encontrarCasilla(punto);
                
                if (casilla != null) {
                    System.out.println("🎯 Casilla seleccionada: [" + casilla.x + "," + casilla.y + "]");
                    ultimaCasillaSeleccionada = casilla;
                    
                    // 🔥 CORRECCIÓN: Verificar que la mano esté conectada
                    if (mano == null) {
                        System.out.println("❌ ERROR: La mano no está conectada al tablero");
                        return;
                    }
                    
                    List<UI_Ficha> fichasSeleccionadas = mano.getFichasSeleccionadas();
                    System.out.println("📋 Fichas seleccionadas en mano: " + fichasSeleccionadas.size());
                    
                    if (!fichasSeleccionadas.isEmpty()) {
                        colocarFichasSeleccionadas(casilla);
                    } else {
                        System.out.println("💡 Selecciona fichas en la mano primero");
                    }
                    
                    repaint(); // Actualizar el resaltado de la casilla
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
    
    private void colocarFichasSeleccionadas(Point casillaInicio) {
        List<UI_Ficha> fichasParaColocar = mano.getFichasSeleccionadas();
        int cantidadFichas = fichasParaColocar.size();
        
        System.out.println("🚀 Intentando colocar " + cantidadFichas + " fichas...");
        
        // Buscar espacio disponible
        Point inicioAjustado = encontrarEspacioDisponible(casillaInicio, cantidadFichas);
        
        if (inicioAjustado != null) {
            System.out.println("📍 Colocando " + cantidadFichas + " fichas desde [" + 
                             inicioAjustado.x + "," + inicioAjustado.y + "]");
            
            // Colocar cada ficha
            for (int i = 0; i < cantidadFichas; i++) {
                UI_Ficha ficha = fichasParaColocar.get(i);
                int columna = inicioAjustado.y + i;
                
                colocarFichaEnCasilla(ficha, inicioAjustado.x, columna);
            }
            
            // Limpiar selección y remover fichas de la mano
            mano.removerFichasSeleccionadas();
            System.out.println("✅ Fichas colocadas exitosamente");
            
        } else {
            System.out.println("❌ No hay espacio suficiente para " + cantidadFichas + " fichas");
            // Mostrar estado del tablero para debug
            mostrarEstadoTablero();
        }
    }
    
    private Point encontrarEspacioDisponible(Point casillaInicio, int cantidadFichas) {
        int fila = casillaInicio.x;
        int columnaInicio = casillaInicio.y;
        
        System.out.println("🔍 Buscando espacio para " + cantidadFichas + " fichas en fila " + fila);
        
        // Intentar hacia la derecha primero
        if (hayEspacioDisponible(fila, columnaInicio, cantidadFichas, true)) {
            System.out.println("   ✅ Espacio encontrado a la derecha: [" + fila + "," + columnaInicio + "]");
            return new Point(fila, columnaInicio);
        }
        
        // Si no hay espacio a la derecha, intentar hacia la izquierda
        int columnaAjustada = columnaInicio - (cantidadFichas - 1);
        if (columnaAjustada >= 0 && hayEspacioDisponible(fila, columnaAjustada, cantidadFichas, true)) {
            System.out.println("   ✅ Espacio encontrado a la izquierda: [" + fila + "," + columnaAjustada + "]");
            return new Point(fila, columnaAjustada);
        }
        
        // Buscar en otras filas
        for (int f = 0; f < 4; f++) {
            if (f != fila) {
                // Buscar desde el inicio de la fila
                for (int c = 0; c <= 10 - cantidadFichas; c++) {
                    if (hayEspacioDisponible(f, c, cantidadFichas, true)) {
                        System.out.println("   🔄 Moviendo a fila " + f + ", columna " + c);
                        return new Point(f, c);
                    }
                }
            }
        }
        
        System.out.println("   ❌ No hay espacio en ninguna fila");
        return null;
    }
    
    private boolean hayEspacioDisponible(int fila, int columnaInicio, int cantidad, boolean buscarDesdeInicio) {
        if (buscarDesdeInicio) {
            // Verificar espacio específico desde columnaInicio
            for (int i = 0; i < cantidad; i++) {
                int columna = columnaInicio + i;
                if (columna >= 10) {
                    System.out.println("      ❌ Columna " + columna + " fuera de límites");
                    return false;
                }
                if (celdasOcupadas[fila][columna] == 1) {
                    System.out.println("      ❌ Celda [" + fila + "," + columna + "] ocupada");
                    return false;
                }
            }
            System.out.println("      ✅ Espacio libre desde [" + fila + "," + columnaInicio + "]");
            return true;
        }
        return false;
    }
    
    private void colocarFichaEnCasilla(UI_Ficha ficha, int fila, int columna) {
        // Crear una copia para el tablero
        UI_Ficha fichaEnTablero = new UI_Ficha(ficha.getNumero(), ficha.getFichaColor());
        fichaEnTablero.setPuedeSerMovida(false);
        
        int cellWidth = getWidth() / 10;
        int cellHeight = getHeight() / 4;
        
        int x = columna * cellWidth + (cellWidth - fichaEnTablero.getWidth()) / 2;
        int y = fila * cellHeight + (cellHeight - fichaEnTablero.getHeight()) / 2;
        
        fichaEnTablero.setBounds(x, y, fichaEnTablero.getPreferredSize().width, fichaEnTablero.getPreferredSize().height);
        add(fichaEnTablero);
        
        // Registrar en el mapa
        String clave = fila + "_" + columna;
        fichasEnTablero.put(clave, fichaEnTablero);
        celdasOcupadas[fila][columna] = 1;
        
        System.out.println("   ✅ Ficha " + ficha.getNumero() + " colocada en [" + fila + "," + columna + "]");
        
        revalidate();
        repaint();
    }
    
    public void mostrarEstadoTablero() {
        System.out.println("🎯 Estado del Tablero:");
        for (int fila = 0; fila < 4; fila++) {
            StringBuilder linea = new StringBuilder();
            for (int columna = 0; columna < 10; columna++) {
                if (celdasOcupadas[fila][columna] == 1) {
                    linea.append("[X]");
                } else {
                    linea.append("[ ]");
                }
            }
            System.out.println("Fila " + fila + ": " + linea.toString());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Dibujar grid del tablero
        int cellWidth = Math.max(getWidth() / 10, 40);
        int cellHeight = Math.max(getHeight() / 4, 40);
        
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
                
                // Marcar celdas ocupadas
                if (celdasOcupadas[fila][columna] == 1) {
                    g2.setColor(new Color(255, 200, 200, 100));
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
