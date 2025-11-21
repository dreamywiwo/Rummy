package itson.rummypresentacion.vista;

import itson.rummydtos.FichaDTO;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class FichaTransferable implements Transferable {
    public static final DataFlavor FICHA_FLAVOR = new DataFlavor(FichaTransferable.class, "Ficha");
    public static final DataFlavor FICHA_DTO_FLAVOR = new DataFlavor(FichaDTO.class, "FichaDTO");
    
    private FichaDTO ficha;
    private ContenedorFichas origen;
    
    public FichaTransferable(FichaDTO ficha, ContenedorFichas origen) {
        this.ficha = ficha;
        this.origen = origen;
    }
    
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{FICHA_FLAVOR, FICHA_DTO_FLAVOR};
    }
    
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return FICHA_FLAVOR.equals(flavor) || FICHA_DTO_FLAVOR.equals(flavor);
    }
    
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (FICHA_FLAVOR.equals(flavor)) {
            return this;
        } else if (FICHA_DTO_FLAVOR.equals(flavor)) {
            return ficha;
        }
        throw new UnsupportedFlavorException(flavor);
    }
    
    public FichaDTO getFicha() {
        return ficha;
    }
    
    public ContenedorFichas getOrigen() {
        return origen;
    }
}